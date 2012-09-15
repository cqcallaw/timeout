package net.brainvitamins.vigilance.server;

import java.util.UUID;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.brainvitamins.vigilance.shared.Recipient;

/**
 * A record of a recipient confirmation request.
 * 
 * The confirmation request is maintained separately from the Recipient because
 * GWT does not directly support java.util.UUID
 * 
 * @param <T>
 */
@PersistenceCapable
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public abstract class ConfirmationRequest<T extends Recipient>
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String id;

	public String getId()
	{
		return id;
	}

	@Persistent
	// unowned relationship: see
	// https://developers.google.com/appengine/docs/java/datastore/jdo/relationships#Unowned_Relationships
	private String recipientKey;

	public String getRecipientKey()
	{
		return recipientKey;
	}

	/**
	 * @see http://stackoverflow.com/q/11026061/577298
	 * @param user
	 * @param recipient
	 * @param address
	 */
	public ConfirmationRequest(T recipient)
	{
		if (recipient == null)
			throw new IllegalArgumentException("recipient cannot be null");

		// according to
		// http://docs.oracle.com/javase/6/docs/api/java/util/UUID.html#randomUUID()
		// UUIDs are generated using a cryptographically strong random number
		// generator.
		// this is important because the ID is made publicly available, and
		// should
		// be unpredictable.
		this.id = UUID.randomUUID().toString();
		this.recipientKey = recipient.getKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ConfirmationRequest [recipient=" + recipientKey + "]";
	}
}
