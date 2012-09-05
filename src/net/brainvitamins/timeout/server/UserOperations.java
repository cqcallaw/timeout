package net.brainvitamins.timeout.server;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.google.appengine.api.users.UserServiceFactory;

public class UserOperations
{
	/**
	 * Returns a detached copy of the current User data object. The activity log
	 * and recipient list are not included.
	 */
	public static User getCurrentUser()
	{
		return getUser(getCurrentUserId());
	}

	public static User getUser(String userId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			User currentUser = pm.getObjectById(User.class, userId);
			pm.makeTransient(currentUser, true);
			return currentUser;
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

	/**
	 * Returns a detached copy of the current user data, with the activity log.
	 */
	public static User getCurrentUserWithActivity()
	{
		return getUserWithActivity(UserOperations.getCurrentUserId());
	}

	public static User getUserWithActivity(String userId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withActivityLog");
		try
		{
			User currentUser = pm.getObjectById(User.class, userId);

			pm.makeTransient(currentUser, true);
			return currentUser;
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

	/**
	 * Returns a detached copy of the current user data, with the activity log.
	 */
	public static User getCurrentUserWithRecipients()
	{
		return getUserWithRecipients(getCurrentUserId());
	}

	public static User getUserWithRecipients(String userId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withRecipients");
		try
		{
			User currentUser = pm.getObjectById(User.class, userId);

			pm.makeTransient(currentUser, true);
			return currentUser;
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

	/**
	 * @see http://stackoverflow.com/a/10604659/577298
	 * @return
	 */
	public static String getCurrentUserId()
	{
		com.google.appengine.api.users.User currentGWTUser = getCurrentGWTUser();

		if (currentGWTUser == null)
			throw new IllegalStateException("Unable to obtain current user.");

		return currentGWTUser.getUserId();
	}

	public static com.google.appengine.api.users.User getCurrentGWTUser()
	{
		return UserServiceFactory.getUserService().getCurrentUser();
	}
}
