package net.brainvitamins.timeout.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Checkin;
import net.brainvitamins.timeout.shared.User;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.UserServiceFactory;

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

		List<Activity> activityLog;

		try
		{
			User currentUser = pm.getObjectById(User.class, userId);

			activityLog = currentUser.getActivityLog();

			if (activity.getClass().equals(Checkin.class))
			{
				Checkin checkin = (Checkin) activity;
				long timeout = checkin.getTimeout();

				// cancel active timeout
				queue.deleteTask(userId);

				// TODO: verify the task was actually deleted.

				// TODO: figure out a way to avoid hardcoding the module path
				// hardcode the module path
				TaskOptions taskOptions = TaskOptions.Builder
						.withUrl("/timeout/timeout").countdownMillis(timeout)
						.param("userId", userId).param("startTime", time)
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
			pm.close();
		}
	}
}
