package net.brainvitamins.timeout.shared;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

//TODO: investigate why this caused problems when it wasn't serializable
@PersistenceCapable
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public abstract class Activity implements Serializable
{
	@SuppressWarnings("unused")
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String key;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3009614156402312511L;

	@Persistent(defaultFetchGroup = "true")
	private Date timestamp;

	public Date getTimestamp()
	{
		return timestamp;
	}

	public Activity()
	{
		timestamp = new Date();
	}

	public Activity(Date timestamp)
	{
		if (timestamp == null)
			throw new IllegalArgumentException("Timestamp cannot be null.");

		this.timestamp = timestamp;
	}

	public abstract String getTypeName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Activity [timestamp=" + timestamp + "]";
	}
}
