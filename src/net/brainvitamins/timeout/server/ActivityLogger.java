package net.brainvitamins.timeout.server;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Checkin;
import net.brainvitamins.timeout.shared.Timeout;
import net.brainvitamins.timeout.shared.User;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.client.GWT;

/*
 * A service designed to log and record user activity to a datastore.
 */

public class ActivityLogger
{
	private DatastoreService datastore;

	public DatastoreService getDatastore()
	{
		return datastore;
	}

	private Queue queue;

	public Queue getQueue()
	{
		return queue;
	}

	private String activityKindIdentifier;

	public String getActivityKindIdentifier()
	{
		return activityKindIdentifier;
	}

	private SimpleDateFormat dateFormat;

	public ActivityLogger(DatastoreService datastore, Queue queue,
			String activityKindIdentifier)
	{
		this.datastore = datastore;
		this.queue = queue;
		this.activityKindIdentifier = activityKindIdentifier;

		dateFormat = new SimpleDateFormat(Constants.DATEFORMAT);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public void logActivity(Activity activity)
	{
		com.google.appengine.api.users.User user = UserServiceFactory
				.getUserService().getCurrentUser();

		logActivity(user.getUserId(), activity);
	}

	public void logActivity(String userId, Activity activity)
	{
		Date timestamp = activity.getTimestamp();
		String time = dateFormat.format(timestamp);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter("id == userIdParam");
		query.declareParameters("String userIdParam");

		List<Activity> activityLog;
		try
		{
			Object rawResults = query.execute(userId);

			List<User> results = (List<User>) rawResults;

			// sanity checks
			if (results.isEmpty() || results.size() > 1)
			{
				throw new InvalidParameterException("Invalid user specified");
			}

			activityLog = results.get(0).getActivityLog();

			if (activity.getClass().equals(Checkin.class))
			{
				Checkin checkin = (Checkin) activity;
				long timeout = checkin.getTimeout();

				// cancel active timeout
				queue.deleteTask(userId);

				// TODO: verify the task was actually deleted.

				// TODO: move this to a RemoteService so we don't have to hardcode the module path
				TaskOptions taskOptions = TaskOptions.Builder
						.withUrl("/timeout/timeout")
						.countdownMillis(timeout).param("userId", userId)
						.param("startTime", time)
						.param("timeout", Long.toString(timeout))
						.taskName(userId);

				queue.add(taskOptions);

				activityLog.add(checkin);
			}
			else
			{
				activityLog.add(activity);
			}
		}
		finally
		{
			query.closeAll();
			pm.close();
		}
	}
}
