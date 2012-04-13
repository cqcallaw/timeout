package net.brainvitamins.presencepoll.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class CheckinServlet extends HttpServlet
{
	private static final long serialVersionUID = -9209388711200753616L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException
	{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		Key activityStoreKey = KeyFactory.createKey(
				Constants.activityKindIdentifier, user.getUserId().toString());
		Entity checkin = new Entity(Constants.activityKindIdentifier,
				activityStoreKey);

		SimpleDateFormat format = new SimpleDateFormat(Constants.DATEFORMAT);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		String time = format.format(new Date());

		long timeout = Long.parseLong(req.getParameter("timeout"));

		checkin.setProperty("type", "checkin");
		checkin.setProperty("time", time);
		checkin.setProperty("timeout", timeout);

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Queue queue = QueueFactory.getDefaultQueue();

		// cancel active timeout
		Query query = new Query(Constants.activityKindIdentifier,
				activityStoreKey).addSort("time",
				Query.SortDirection.DESCENDING).addFilter("type",
				Query.FilterOperator.EQUAL, "checkin");

		List<Entity> activity = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(1));

		if (!activity.isEmpty())
		{
			Entity latestCheckin = activity.get(0);
			String checkinTaskName = latestCheckin.getProperty("taskName")
					.toString();
			queue.deleteTask(checkinTaskName);
		}

		// TODO: verify the task was actually deleted.

		TaskOptions taskOptions = TaskOptions.Builder
				.withUrl("/presence_poll/timeout")
				.countdownMillis(timeout)
				.param("userId", user.getUserId())
				.param("userEmail", user.getEmail())
				.param("startTime", time)
				.param("timeout", Long.toString(timeout));
		TaskHandle taskHandle = queue.add(taskOptions);

		checkin.setProperty("taskName", taskHandle.getName());

		datastore.put(checkin);

		resp.sendRedirect("/");
	}
}
