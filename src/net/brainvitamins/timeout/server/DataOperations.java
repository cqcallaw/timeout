package net.brainvitamins.timeout.server;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;


import com.google.appengine.api.users.UserServiceFactory;

public class DataOperations
{
	/*
	 * Returns a detached copy of the current User data object. The activity log and recipient list are not included.
	 */
	public static User getCurrentUser()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			User currentUser = pm.getObjectById(User.class, getGWTUser().getUserId());
			User detached = pm.detachCopy(currentUser);
			return detached;
		}
		catch (JDOObjectNotFoundException e)
		{
			return null;
		}
		finally
		{
			pm.close();
		}
	}

	/*
	 * Returns a detached copy of the current user data, with the activity log.
	 */
	public static User getCurrentUserWithActivity()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withActivityLog");
		try
		{
			User currentUser = pm.getObjectById(User.class, getGWTUser().getUserId());
			
			User detached = pm.detachCopy(currentUser);
			return detached;
		}
		catch (JDOObjectNotFoundException e)
		{
			return null;
		}
		finally
		{
			pm.close();
		}
	}

	/*
	 * Returns a detached copy of the current user data, with the activity log.
	 */
	public static User getCurrentUserWithRecipients()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withRecipients");
		try
		{
			User currentUser = pm.getObjectById(User.class, getGWTUser().getUserId());
			
			User detached = pm.detachCopy(currentUser);
			return detached;
		}
		catch (JDOObjectNotFoundException e)
		{
			return null;
		}
		finally
		{
			pm.close();
		}
	}
	
	private static com.google.appengine.api.users.User getGWTUser()
	{
		return UserServiceFactory.getUserService().getCurrentUser();
	}
}
