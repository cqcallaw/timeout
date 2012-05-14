package net.brainvitamins.timeout.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.PersistenceManager;

import net.brainvitamins.timeout.client.services.RecipientService;
import net.brainvitamins.timeout.shared.Recipient;
import net.brainvitamins.timeout.shared.User;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RecipientServiceImpl extends RemoteServiceServlet implements
		RecipientService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6581374344337957491L;

	public static final String recipientKindIdentifier = "Recipient";

	@Override
	public void addRecipient(Recipient recipient)
	{
		com.google.appengine.api.users.User user = UserServiceFactory
				.getUserService().getCurrentUser();

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			User currentUser = pm.getObjectById(User.class, user.getUserId());
			currentUser.getRecipients().add(recipient);
		}
		finally
		{
			pm.close();
		}
	}

	@Override
	public List<Recipient> getRecipients()
	{
		List<Recipient> result = new ArrayList<Recipient>(DataOperations
				.getCurrentUserWithRecipients().getRecipients());

		// sort by name, descending
		Collections.sort(result, new Comparator<Recipient>()
		{
			@Override
			public int compare(Recipient o1, Recipient o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		return result;
	}

	@Override
	public boolean removeRecipient(Recipient recipient)
	{
		com.google.appengine.api.users.User user = UserServiceFactory
				.getUserService().getCurrentUser();

		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withRecipients");

		try
		{
			User currentUser = pm.getObjectById(User.class, user.getUserId());

			Recipient ref = null;
			boolean result = false;

			for (Recipient r : currentUser.getRecipients())
			{
				if (r.equals(recipient)) ref = r;
			}

			if (ref != null)
			{
				result = currentUser.getRecipients().remove(ref);
			}

			return result;
		}
		finally
		{
			pm.close();
		}
	}
}
