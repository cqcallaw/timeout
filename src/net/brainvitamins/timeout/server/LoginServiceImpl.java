package net.brainvitamins.timeout.server;

import javax.jdo.PersistenceManager;

import net.brainvitamins.timeout.shared.LoginInfo;
import net.brainvitamins.timeout.shared.User;
import net.brainvitamins.timeout.shared.services.LoginService;

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
			User userDataObject = DataOperations.getCurrentUser();
			if (userDataObject == null)
			{
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try
				{
					pm.makePersistent(new User(user.getUserId(), user
							.getNickname()));
				}
				finally
				{
					pm.close();
				}
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