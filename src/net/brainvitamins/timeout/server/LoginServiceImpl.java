package net.brainvitamins.timeout.server;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.brainvitamins.timeout.client.services.LoginService;
import net.brainvitamins.timeout.shared.LoginInfo;
import net.brainvitamins.timeout.shared.User;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6912694102250330793L;

	public LoginInfo login(String requestUri)
	{
		UserService userService = UserServiceFactory.getUserService();
		com.google.appengine.api.users.User user = userService.getCurrentUser();

		LoginInfo loginInfo = new LoginInfo();

		if (user != null)
		{
			// make sure the user exists in the db
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery(User.class);
			query.setFilter("id == userIdParam");
			query.declareParameters("String userIdParam");

			try
			{
				Object rawResults = query.execute(user.getUserId());
				List<User> results = (List<User>) rawResults;
				if (results.isEmpty())
				{
					pm.makePersistent(new User(user.getUserId(), user.getNickname()));
				}
			}
			finally
			{
				query.closeAll();
				pm.close();
			}

			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
		}
		else
		{
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}

		return loginInfo;
	}
}