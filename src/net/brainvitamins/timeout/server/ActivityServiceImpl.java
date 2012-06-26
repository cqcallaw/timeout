package net.brainvitamins.timeout.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Cancellation;
import net.brainvitamins.timeout.shared.Checkin;
import net.brainvitamins.timeout.shared.operations.CreateOperation;
import net.brainvitamins.timeout.shared.services.ActivityService;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ActivityServiceImpl extends RemoteServiceServlet implements
		ActivityService
{
	private Queue queue = QueueFactory.getDefaultQueue();

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
			System.out.println("No activity found for "
					+ currentUser.getNickname());
			return activityLog;
		}

		// TODO: this may be unnecessary and inefficient.
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

			// touch timeout field in Checkin object to make sure it's
			// loaded. *grumble*
			if (activity.getClass().equals(Checkin.class))
			{
				((Checkin) activity).getTimeout();
			}

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
		User user = UserOperations.getCurrentUser();

		if (user == null)
		{
			throw new IllegalStateException("Unable to obtain current user.");
		}

		String userId = user.getId();

		String sessionId = getThreadLocalRequest().getSession().getId();

		// cancel active timeout
		cancelCheckin(userId);

		// TODO: figure out a way to avoid hardcoding the module path
		// hardcode the module path
		TaskOptions taskOptions = TaskOptions.Builder
				.withUrl("/timeout/timeout").countdownMillis(timeout)
				.param("userId", userId).param("startTime", time)
				.param("sourceSessionId", sessionId)
				.param("timeout", Long.toString(timeout)).taskName(userId);

		queue.add(taskOptions);

		ActivityOperations.log(userId, checkin);

		PushOperations.pushToListener(sessionId, new CreateOperation<Activity>(
				checkin));
	}

	@Override
	public void cancelCheckin() throws IllegalStateException
	{
		User user = UserOperations.getCurrentUserWithActivity();

		if (user == null)
			throw new IllegalStateException("Cannot obtain current user.");

		String userId = user.getId();

		cancelCheckin(userId);
		List<Activity> activityLog = user.getActivityLog();

		if (activityLog.size() > 0)
		{
			Activity lastActivity = activityLog.get(activityLog.size() - 1);
			if (lastActivity instanceof Checkin)
			{
				Cancellation cancellation = new Cancellation();
				ActivityOperations.log(userId, cancellation);

				PushOperations.pushToListener(getThreadLocalRequest()
						.getSession().getId(), new CreateOperation<Activity>(
						cancellation));
			}
			else
			{
				throw new IllegalStateException(
						"Cannot cancel checkin if no checkin is active.");
			}
		}
		else
		{
			throw new IllegalStateException(
					"Cannot cancel checkin on an empty activity log.");
		}
	}

	private void cancelCheckin(String userId)
	{
		// TODO: verify the task was actually deleted.
		queue.deleteTask(userId);
	}
}
