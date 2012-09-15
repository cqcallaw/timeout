package net.brainvitamins.vigilance.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import net.brainvitamins.vigilance.shared.Activity;

public class ActivityOperations
{
	private static final Logger log = Logger
			.getLogger(ActivityOperations.class.getName());

	public static void log(String userId, Activity activity)
	{
		log.log(Level.FINE, "Logging " + activity.toString());
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
