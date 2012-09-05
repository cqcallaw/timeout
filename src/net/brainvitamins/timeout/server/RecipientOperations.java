package net.brainvitamins.timeout.server;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;

public class RecipientOperations
{
	private static final Logger logger = Logger
			.getLogger(RecipientOperations.class.getName());

	/**
	 * @param recipient
	 * @param userId
	 *            the hashed userId
	 */
	public static void addRecipient(Recipient recipient, String userId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Transaction tx = pm.currentTransaction();
		try
		{
			User currentUser = pm.getObjectById(User.class, userId);
			if (currentUser == null)
			{
				throw new IllegalArgumentException("Invalid userId.");
			}

			Set<Recipient> recipients = currentUser.getRecipients();

			recipients.add(recipient);
		}

		finally
		{
			if (tx.isActive())
			{
				logger.log(Level.SEVERE, "Adding " + recipient + " failed.");
				tx.rollback();
			}
			pm.close();
		}
	}

	/**
	 * @param recipient
	 * @throws UnsupportedEncodingException
	 *             if
	 */
	public static void updateRecipient(EmailRecipient updatedRecipient,
			User user, URL url) throws UnsupportedEncodingException
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			EmailRecipient currentRecipient = pm.getObjectById(
					EmailRecipient.class, updatedRecipient.getKey());

			if (currentRecipient == null)
			{
				throw new IllegalArgumentException("Invalid recipient.");
			}

			Transaction tx = pm.currentTransaction();
			try
			{
				tx.begin();
				pm.makePersistent(updatedRecipient); // overwrite old object
				tx.commit();
			}
			finally
			{
				if (tx.isActive())
				{
					logger.log(Level.SEVERE, "Update of " + currentRecipient
							+ " to " + updatedRecipient + " failed.");
					tx.rollback();
				}
			}

			if (!currentRecipient.getAddress().equals(
					updatedRecipient.getAddress()))
			{
				ConfirmationRequestOperations.sendConfirmationRequest(
						updatedRecipient, user, url);
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
	public static Recipient getDatabaseReference(Recipient recipient,
			Set<Recipient> recipients)
	{
		Recipient ref = null;

		for (Recipient r : recipients)
		{
			if (r.getKey().equals(recipient.getKey())) ref = r;
		}
		return ref;
	}
}
