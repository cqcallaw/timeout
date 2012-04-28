package net.brainvitamins.timeout.server;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import net.brainvitamins.timeout.shared.User;

import com.google.appengine.api.users.UserServiceFactory;

public class DataOperations
{
	public static User GetCurrentUser()
	{
		com.google.appengine.api.users.User user = UserServiceFactory
				.getUserService().getCurrentUser();

		// TODO: refactor the JDO query that gets the user into a single method
		PersistenceManager pm = PMF.get().getPersistenceManager();
		// Query query = pm.newQuery(User.class);
		// query.setFilter("id == userIdParam");
		// query.declareParameters("String userIdParam");
		//
		// Object rawResults = query.execute(user.getUserId());
		// List<User> results = (List<User>) rawResults;
		//
		// // sanity checks
		// if (results.isEmpty() || results.size() > 1)
		// {
		// throw new InvalidParameterException("Invalid user specified");
		// }
		//
		// User currentUser = results.get(0);

		try
		{
			User currentUser = pm.getObjectById(User.class, user.getUserId());
//			if (currentUser == null)
//				throw new InvalidParameterException("Invalid user specified");

			return currentUser;
		}
		catch (JDOObjectNotFoundException e)
		{
			return null;
		}
	}
}
