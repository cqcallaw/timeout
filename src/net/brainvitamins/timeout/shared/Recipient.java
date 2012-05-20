package net.brainvitamins.timeout.shared;

import java.io.Serializable;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.validation.constraints.NotNull;

@PersistenceCapable(detachable = "true")
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public abstract class Recipient implements Serializable
{
	private static final long serialVersionUID = 8775141775408581192L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String key;

	public String getKey()
	{
		return key;
	}

	/*
	 * Flag to indicate whether the recipient has confirmed they wish to receive notifications
	 */
	// TODO: think of how this could be re-implemented as an enumeration
	@Persistent
	private boolean verified;

	public boolean isVerified()
	{
		return verified;
	}

	@Persistent
	private String name;

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	public Recipient()
	{
		this("", false);
	}

	public Recipient(@NotNull String name)
	{
		this(name, false);
	}

	public Recipient(@NotNull String name, boolean verified)
	{
		if (name == null)
			throw new IllegalArgumentException("Name can't be null");

		this.verified = verified;
		this.name = name;
	}

	/*
	 * Protected constructor for withProperty method chaining
	 */
	protected Recipient(@NotNull String name, boolean verified, String dbKey)
	{
		this(name, verified);
		this.key = dbKey;
	}

	public abstract Recipient withName(String name);

	public abstract Recipient withVerified(boolean verified);

	public abstract Recipient clone();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (verified ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Recipient other = (Recipient) obj;
		if (key == null)
		{
			if (other.key != null) return false;
		}
		else if (!key.equals(other.key)) return false;
		if (name == null)
		{
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		if (verified != other.verified) return false;
		return true;
	}
}
