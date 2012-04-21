package net.brainvitamins.timeout.server;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.brainvitamins.timeout.client.ActivityService;
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
		com.google.appengine.api.users.User user = UserServiceFactory
				.getUserService().getCurrentUser();

		// TODO: refactor the JDO query that gets the user into a single method
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter("id == userIdParam");
		query.declareParameters("String userIdParam");

		List<Activity> activityLog = new ArrayList<Activity>();
		try
		{
			Object rawResults = query.execute(user.getUserId());
			List<User> results = (List<User>) rawResults;

			// sanity checks
			if (results.isEmpty() || results.size() > 1)
			{
				throw new InvalidParameterException("Invalid user specified");
			}

			User currentUser = results.get(0);

			// sort items in descending order
			// would be nice to do this as part of the JDO query...
			Collections.sort(currentUser.getActivityLog(),
					new Comparator<Activity>()
					{
						@Override
						public int compare(Activity o1, Activity o2)
						{
							return o2.getTimestamp().compareTo(
									o1.getTimestamp());
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

		}
		// TODO: handle data exceptions
		finally
		{
			query.closeAll();
			pm.close();
		}

		return activityLog;
	}
}
