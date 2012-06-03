package net.brainvitamins.timeout.server;

import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

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

			Recipient dbReference = null;
			Transaction tx = pm.currentTransaction();
			try
			{
				dbReference = getDatabaseReference(recipient, recipients);

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
					System.out.println("Update of " + dbReference + " to "
							+ recipient + " failed.");
					tx.rollback();
				}
			}
		}
		finally
		{
			pm.close();
		}
	}

	public static void pushToClient(String sessionId, Recipient recipient,
			DataOperation operation)
	{
		ChannelService channelService = ChannelServiceFactory
				.getChannelService();

		String channelKey = sessionId + ":recipient";

		channelService.sendMessage(new ChannelMessage(channelKey, operation
				.toString()));

		channelService.sendMessage(new ChannelMessage(channelKey, recipient
				.toString()));
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
