package net.brainvitamins.timeout.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import net.brainvitamins.timeout.client.ActivityService;
import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Checkin;
import net.brainvitamins.timeout.shared.Timeout;

public class ActivityServiceImpl extends RemoteServiceServlet implements
		ActivityService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3820757361541824185L;

	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	@Override
	public List<Activity> getActivityLog(int sizeLimit)
	{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		Key activityStoreKey = KeyFactory.createKey("Activity", user
				.getUserId().toString());

		Query query = new Query("Activity", activityStoreKey).addSort("time",
				Query.SortDirection.DESCENDING);

		List<Entity> activityEntries = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(sizeLimit));

		List<Activity> activityLog = new ArrayList<Activity>();

		for (Entity entity : activityEntries)
		{
			Activity activity = reconstituteActivity(entity);
			if (activity != null) activityLog.add(activity);
		}

		return activityLog;
	}

	private Activity reconstituteActivity(Entity entity)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATEFORMAT);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		if (entity.getProperty("type").equals("checkin"))
		{
			Date timestamp = null;
			try
			{
				timestamp = dateFormat.parse((String) entity
						.getProperty("time"));
			}
			catch (ParseException e)
			{
				System.out.println("Error parsing date.");
				return null;
			}

			return new Checkin(timestamp, (Long) entity.getProperty("timeout"));
		}
		else if (entity.getProperty("type").equals("timeout"))
		{
			Date timestamp = null;
			Date startTime = null;
			try
			{
				timestamp = dateFormat.parse((String) entity
						.getProperty("time"));
				startTime = dateFormat.parse((String) entity
						.getProperty("startTime"));
			}
			catch (ParseException e)
			{
				System.out.println("Error parsing date.");
				return null;
			}

			return new Timeout(timestamp, (Long) entity.getProperty("timeout"),
					startTime, (String) entity.getProperty("userId"),
					(String) entity.getProperty("userEmail"));
		}
		else
			return null;
	}
}
