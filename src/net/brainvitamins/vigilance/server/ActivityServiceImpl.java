package net.brainvitamins.vigilance.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.brainvitamins.vigilance.shared.Activity;
import net.brainvitamins.vigilance.shared.Cancellation;
import net.brainvitamins.vigilance.shared.Checkin;
import net.brainvitamins.vigilance.shared.operations.CreateOperation;
import net.brainvitamins.vigilance.shared.services.ActivityService;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ActivityServiceImpl extends RemoteServiceServlet implements
		ActivityService
{
	private static final Logger logger = Logger
			.getLogger(ActivityServiceImpl.class.getName());

	private Queue queue = QueueFactory.getQueue("activity");

	private static final long serialVersionUID = 3820757361541824185L;

	@Override
	public List<Activity> getActivityLog(int sizeLimit)
	{
		if (sizeLimit < 1)
			throw new IllegalArgumentException(
					"sizeLimit must greater than or equal to 1.");

		User currentUser = UserOperations.getCurrentUserWithActivity();

		if (currentUser == null)
			throw new IllegalStateException("Cannot obtain current user.");

		List<Activity> activityLog = new ArrayList<Activity>();

		if (currentUser.getActivityLog() == null)
		{
			logger.log(Level.INFO,
					"No activity found for " + currentUser.getNickname());
			return activityLog;
		}

		// TODO: this may be unnecessary and inefficient.
		// descending sort
		Collections.sort(currentUser.getActivityLog(),
				new Comparator<Activity>()
				{
					@Override
					public int compare(Activity o1, Activity o2)
					{
						return o2.getTimestamp().compareTo(o1.getTimestamp());
					}
				});

		int i = 0;
		for (Activity activity : currentUser.getActivityLog())
		{
			if (i >= sizeLimit) break;

			activityLog.add(activity);
			i++;
		}

		return activityLog;
	}

	@Override
	public void checkin(long timeout)
	{
		if (timeout < 1)
			throw new IllegalArgumentException(
					"Parameter timeout cannot be less than one (was it defined?)");

		Date timestamp = new Date();
		Checkin checkin = new Checkin(timestamp, timeout);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				Constants.INTERNALDATEFORMAT);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String time = dateFormat.format(timestamp);
		// String userId = Utilities.getCurrentUserHashedId();
		User user = UserOperations.getCurrentUserWithActivity();

		if (user == null)
		{
			throw new IllegalStateException("Unable to obtain current user.");
		}

		String userId = user.getId();

		String sessionId = getThreadLocalRequest().getSession().getId();

		// TODO: figure out a way to avoid hardcoding the module path
		// hardcode the module path
		TaskOptions taskOptions = TaskOptions.Builder
				.withUrl("/vigilance/timeout").countdownMillis(timeout)
				.param("userId", userId).param("startTime", time)
				.param("sourceSessionId", sessionId)
				.param("timeout", Long.toString(timeout))
				.taskName(getTaskId(user, checkin));

		cancelCheckin(user);

		// TODO: there's a race condition here...
		queue.add(taskOptions);

		ActivityOperations.log(userId, checkin);

		PushOperations.pushToListener(sessionId, new CreateOperation<Activity>(
				checkin));
	}

	/**
	 * @param user
	 */
	private void cancelCheckin(User user)
	{
		List<Activity> activityLog = user.getActivityLog();

		if (!activityLog.isEmpty())
		{
			Activity lastActivity = activityLog.get(0);
			if (lastActivity instanceof Checkin)
			{
				queue.deleteTask(getTaskId(user, lastActivity));
			}
			// urgh no feedback if the consumer's trying to cancel when there's
			// nothing to cancel...
		}
	}

	@Override
	public void cancelCheckin() throws IllegalStateException
	{
		User user = UserOperations.getCurrentUserWithActivity();

		if (user == null)
			throw new IllegalStateException("Cannot obtain current user.");

		cancelCheckin(user);

		Cancellation cancellation = new Cancellation();
		ActivityOperations.log(user.getId(), cancellation);

		PushOperations.pushToListener(getThreadLocalRequest().getSession()
				.getId(), new CreateOperation<Activity>(cancellation));
	}

	private String getTaskId(User user, Activity activity)
	{
		return user.getId() + activity.getTimestamp().hashCode();
	}
}
