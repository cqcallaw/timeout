package net.brainvitamins.timeout.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.NotNull;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;
import net.brainvitamins.timeout.shared.User;
import net.brainvitamins.timeout.shared.services.RecipientService;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RecipientServiceImpl extends RemoteServiceServlet implements
		RecipientService
{
	private static final long serialVersionUID = 6581374344337957491L;

	public static final String recipientKindIdentifier = "Recipient";

	@Override
	public void saveRecipient(@NotNull Recipient recipient)
			throws IllegalArgumentException
	{
		// validation
		if (recipient == null)
			throw new IllegalArgumentException("Recipient cannot be null.");

		if (recipient.getName() == null)
			throw new IllegalArgumentException("Name cannot be null.");

		if (recipient.getName().equals(""))
			throw new IllegalArgumentException("Name cannot be empty.");

		com.google.appengine.api.users.User user = UserServiceFactory
				.getUserService().getCurrentUser();

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			User currentUser = pm.getObjectById(User.class, user.getUserId());

			Set<Recipient> recipients = currentUser.getRecipients();

			if (recipient.getKey() == null) // new recipient--add it
			{
				recipients.add(recipient);
			}
			else
			{
				Transaction tx = pm.currentTransaction();
				try
				{
					// doing a getObjectById on the recipient causes DataNucleus
					// to complain about multiple entity groups in a single
					// transaction
					// Object old = pm.getObjectById(recipient.getClass(),
					// recipient.getKey());

					Recipient dbReference = getDatabaseReference(recipient,
							recipients);

					// this is an elaborate hack to maintain the immutability of
					// (most) shared types (User being the exception)
					// The add precedes the remove so the remove operation
					// doesn't run afoul of
					// "Cannot read fields from a deleted object" errors from
					// DataNucleus.
					// The logic goes:
					// -add an exact duplicate (sans database key)
					// -remove the previous persisted Recipient by reference
					// there's almost certainly a better way to handle this;
					// I just don't see it right now.

					if (dbReference != null)
					{
						tx.begin();
						recipients.add(recipient.clone());
						recipients.remove(dbReference);
						tx.commit();
					}
					else
					{
						throw new IllegalArgumentException("Invalid recipient.");
					}
				}
				finally
				{
					if (tx.isActive())
					{
						// TODO: better logging support
						System.out.println("Transaction failed.");
						tx.rollback();
					}
				}
			}
		}
		finally
		{
			pm.close();
		}
	}

	/**
	 * Get a reference to the object in a Set that comes from a database store.
	 * This is necessary for properly deleting an item from a persisted Set.
	 * (see http://stackoverflow.com/a/10577552/577298)
	 * 
	 * @param recipient
	 * @param recipients
	 * @return
	 */
	private Recipient getDatabaseReference(Recipient recipient,
			Set<Recipient> recipients)
	{
		Recipient ref = null;

		for (Recipient r : recipients)
		{
			if (r.getKey().equals(recipient.getKey())) ref = r;
		}
		return ref;
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

			boolean result = false;
			Recipient ref = getDatabaseReference(recipient,
					currentUser.getRecipients());

			if (ref != null) result = currentUser.getRecipients().remove(ref);

			return result;
		}
		finally
		{
			pm.close();
		}
	}

	@Override
	public void saveRecipient(EmailRecipient recipient)
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

		// TODO: send verification email
		// TODO: handle email failure modes:
		// -delivery fails
		// -delivery fails remotely (initial send is a success)
		// -delivery succeeds but adding the recipient to the app engine
		// database fails

		saveRecipient((Recipient) recipient);
	}
}
