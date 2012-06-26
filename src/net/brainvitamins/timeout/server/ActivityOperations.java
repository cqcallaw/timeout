package net.brainvitamins.timeout.server;

import javax.jdo.PersistenceManager;

import net.brainvitamins.timeout.shared.Activity;

public class ActivityOperations
{
	public static void log(String userId, Activity activity)
	{
		System.out.println("Logging " + activity.toString());
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
