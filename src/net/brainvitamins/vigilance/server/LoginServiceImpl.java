package net.brainvitamins.vigilance.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import net.brainvitamins.vigilance.shared.LoginInfo;
import net.brainvitamins.vigilance.shared.services.LoginService;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService
{
	private static final Logger logger = Logger
			.getLogger(LoginServiceImpl.class.getName());

	private static final long serialVersionUID = 6912694102250330793L;

	public LoginInfo login(String requestUri)
	{
		UserService userService = UserServiceFactory.getUserService();
		com.google.appengine.api.users.User user = userService.getCurrentUser();

		LoginInfo loginInfo = new LoginInfo();

		if (user != null)
		{
			User userDataObject = UserOperations.getCurrentUser();
			if (userDataObject == null)
			{
				PersistenceManager pm = PMF.get().getPersistenceManager();
				Transaction tx = pm.currentTransaction();
				User newUser = new User(user.getUserId(), user.getNickname());
				try
				{
					tx.begin();
					pm.makePersistent(newUser);
					tx.commit();
				}
				finally
				{
					if (tx.isActive())
					{
						logger.log(Level.SEVERE, "Failed to create user.");
						tx.rollback();
					}

					logger.log(Level.FINEST, "Added " + newUser);
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