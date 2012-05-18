package net.brainvitamins.timeout.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;
import net.brainvitamins.timeout.shared.User;
import net.brainvitamins.timeout.shared.services.RecipientService;

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
			throws IllegalArgumentException
	{
		// validation
		if (recipient.getName() == null)
			throw new IllegalArgumentException("Name cannot be null.");

		if (recipient.getName().equals(""))
			throw new IllegalArgumentException(
					"Name cannot be empty.");

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

			// ref: http://stackoverflow.com/a/10577552/577298
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

	@Override
	public void addRecipient(EmailRecipient recipient)
			throws IllegalArgumentException
	{
		try
		{
			new InternetAddress(recipient.getAddress());
		}
		catch (AddressException e)
		{
			throw new IllegalArgumentException("Invalid email address.");
		}

		//TODO: send confirmation email
		//TODO: handle email failure modes:
		//-delivery fails
		//-delivery fails remotely (initial send is a success)
		//-delivery succeeds but adding the recipient to the app engine database fails

		addRecipient((Recipient) recipient);
	}
}
