package net.brainvitamins.timeout.server;

import java.util.Set;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import net.brainvitamins.timeout.shared.Recipient;

public class DataOperations
{
	/**
	 * Returns a detached copy of the current User data object. The activity log
	 * and recipient list are not included.
	 */
	public static User getCurrentUser()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			User currentUser = pm.getObjectById(User.class,
					Utilities.getCurrentUserHashedId());
			User detached = pm.detachCopy(currentUser);
			return detached;
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
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withActivityLog");
		try
		{
			User currentUser = pm.getObjectById(User.class,
					Utilities.getCurrentUserHashedId());

			User detached = pm.detachCopy(currentUser);
			return detached;
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
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withRecipients");
		try
		{
			User currentUser = pm.getObjectById(User.class,
					Utilities.getCurrentUserHashedId());

			User detached = pm.detachCopy(currentUser);
			return detached;
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
	 * @param recipient
	 * @param userId
	 *            the hashed userId
	 */
	public static void addRecipient(Recipient recipient, String userId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			User currentUser = pm.getObjectById(User.class, userId);

			Set<Recipient> recipients = currentUser.getRecipients();

			recipients.add(recipient);
		}
		finally
		{
			pm.close();
		}
	}

	/**
	 * @param recipient
	 * @param userId
	 *            the hashed userId
	 */
	public static void updateRecipient(Recipient recipient, String userId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			User currentUser = pm.getObjectById(User.class, userId);

			Set<Recipient> recipients = currentUser.getRecipients();

			Transaction tx = pm.currentTransaction();
			try
			{
				Recipient dbReference = getDatabaseReference(recipient,
						recipients);

				// this is an elaborate hack to maintain the immutability of
				// shared data types
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
