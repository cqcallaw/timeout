package net.brainvitamins.timeout.server;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.brainvitamins.timeout.client.services.ActivityService;
import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Checkin;
import net.brainvitamins.timeout.shared.User;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ActivityServiceImpl extends RemoteServiceServlet implements
		ActivityService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3820757361541824185L;

	@Override
	public List<Activity> getActivityLog(int sizeLimit)
	{
		User currentUser = DataOperations.GetCurrentUser();

		List<Activity> activityLog = new ArrayList<Activity>();

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
}
