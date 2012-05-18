package net.brainvitamins.timeout.server;

import javax.jdo.PersistenceManager;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.User;

public class ActivityLogger
{
	public void logActivity(String userId, Activity activity)
	{
		System.out.println("Logging activity " + activity.toString());
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withActivityLog");
		try
		{
			User currentUser = pm.getObjectById(User.class, userId);
			currentUser.getActivityLog().add(activity);
		}
		finally
		{
			pm.close();
		}
	}
}
