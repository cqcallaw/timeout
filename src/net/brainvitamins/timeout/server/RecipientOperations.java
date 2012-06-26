package net.brainvitamins.timeout.server;

import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;

public class RecipientOperations
{
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
				// TODO: better logging support
				System.out.println("Adding " + recipient + " failed.");
				tx.rollback();
			}
			pm.close();
		}
	}

	/**
	 * @param recipient
	 */
	public static void updateRecipient(EmailRecipient updatedRecipient)
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
					// TODO: better logging support
					System.out.println("Update of " + currentRecipient + " to "
							+ updatedRecipient + " failed.");
					tx.rollback();
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
