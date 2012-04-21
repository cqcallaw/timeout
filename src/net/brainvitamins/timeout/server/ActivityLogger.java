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

	// TODO: folder this code into the ActivityServiceImpl class
	// public List<Activity> getActivityLog(int sizeLimit)
	// {
	// UserService userService = UserServiceFactory.getUserService();
	// User user = userService.getCurrentUser();
	//
	// Key activityStoreKey = KeyFactory.createKey(activityKindIdentifier,
	// user.getUserId().toString());
	//
	// Query query = new Query(activityKindIdentifier, activityStoreKey)
	// .addSort("time", Query.SortDirection.DESCENDING);
	//
	// List<Entity> activityEntries = datastore.prepare(query).asList(
	// FetchOptions.Builder.withLimit(sizeLimit));
	//
	// List<Activity> activityLog = new ArrayList<Activity>();
	//
	// for (Entity entity : activityEntries)
	// {
	// Activity activity = reconstituteActivity(entity);
	// if (activity != null) activityLog.add(activity);
	// }
	//
	// return activityLog;
	// }

	// private Activity reconstituteActivity(Entity entity)
	// {
	// if (entity.getProperty("type").equals("checkin"))
	// {
	// Date timestamp = null;
	// try
	// {
	// timestamp = dateFormat.parse((String) entity
	// .getProperty("time"));
	// }
	// catch (ParseException e)
	// {
	// System.out.println("Error parsing date.");
	// return null;
	// }
	//
	// return new Checkin(timestamp, (Long) entity.getProperty("timeout"));
	// }
	// else if (entity.getProperty("type").equals("timeout"))
	// {
	// Date timestamp = null;
	// Date startTime = null;
	// try
	// {
	// timestamp = dateFormat.parse((String) entity
	// .getProperty("time"));
	// startTime = dateFormat.parse((String) entity
	// .getProperty("startTime"));
	// }
	// catch (ParseException e)
	// {
	// System.out.println("Error parsing date.");
	// return null;
	// }
	//
	// return new Timeout(timestamp, (Long) entity.getProperty("timeout"),
	// startTime);
	// }
	// else
	// return null;
	// }

	// public void logActivity(Checkin checkin)
	// {
	// // UserService userService = UserServiceFactory.getUserService();
	// // User user = userService.getCurrentUser();
	// //
	// Date timestamp = checkin.getTimestamp();
	// long timeout = checkin.getTimeout();
	// String time = dateFormat.format(timestamp);
	// //
	// // Key activityStoreKey = KeyFactory.createKey(activityKindIdentifier,
	// // user.getUserId().toString());
	// // Entity entry = new Entity(activityKindIdentifier, activityStoreKey);
	// //
	// //
	// // entry.setProperty("type", "checkin");
	// // entry.setProperty("time", time);
	// // entry.setProperty("timeout", timeout);
	//
	// com.google.appengine.api.users.User user = UserServiceFactory
	// .getUserService().getCurrentUser();
	//
	// PersistenceManager pm = Constants.pmfInstance.getPersistenceManager();
	// Query query = pm.newQuery(User.class);
	// query.setFilter("userId == userIdParam");
	// query.declareParameters("String userIdParam");
	//
	// List<Activity> activityLog;
	// try
	// {
	// Object rawResults = query.execute(user.getUserId());
	// List<User> results = (List<User>) rawResults;
	//
	// // sanity checks
	// if (results.isEmpty() || results.size() > 1)
	// {
	// throw new InvalidParameterException("Invalid user specified");
	// }
	//
	// activityLog = results.get(0).getActivityLog();
	//
	// // cancel active timeout
	// queue.deleteTask(user.getUserId());
	//
	// // TODO: verify the task was actually deleted.
	//
	// TaskOptions taskOptions = TaskOptions.Builder
	// .withUrl("/hellohello/timeout").countdownMillis(timeout)
	// .param("userId", user.getUserId())
	// .param("userEmail", user.getEmail())
	// .param("startTime", time)
	// .param("timeout", Long.toString(timeout))
	// .taskName(user.getUserId());
	//
	// queue.add(taskOptions);
	//
	// activityLog.add(checkin);
	// }
	// finally
	// {
	// query.closeAll();
	// pm.close();
	// }
	//
	// // entry.setProperty("taskName", taskHandle.getName());
	//
	// // datastore.put(entry);
	// }

	// public void logActivity(String userId, Timeout timeout)
	// {
	// Key activityStoreKey = KeyFactory.createKey(activityKindIdentifier,
	// userId);
	//
	// Entity timeoutEntry = new Entity(activityKindIdentifier,
	// activityStoreKey);
	//
	// timeoutEntry.setProperty("type", "timeout");
	// timeoutEntry.setProperty("time",
	// dateFormat.format(timeout.getTimestamp()));
	// timeoutEntry.setProperty("startTime",
	// dateFormat.format(timeout.getStartTime()));
	// timeoutEntry.setProperty("timeout", timeout.getTimeout());
	//
	// datastore.put(timeoutEntry);
	// }

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

				TaskOptions taskOptions = TaskOptions.Builder
						.withUrl("/hellohello/timeout")
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
