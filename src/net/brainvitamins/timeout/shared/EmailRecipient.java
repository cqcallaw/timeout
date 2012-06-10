package net.brainvitamins.timeout.shared;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.validation.constraints.NotNull;

@PersistenceCapable(detachable = "true")
public class EmailRecipient extends Recipient implements Serializable
{
	private static final long serialVersionUID = -2341504040837658628L;

	@Persistent
	private String address;

	public String getAddress()
	{
		return address;
	}

	/**
	 * Flag to indicate whether the recipient has confirmed they wish to receive
	 * notifications
	 */
	// TODO: think of how this could be re-implemented as an enumeration
	@Persistent
	private boolean verified;

	public boolean isVerified()
	{
		return verified;
	}

	public EmailRecipient()
	{
		this("", "");
	}

	public EmailRecipient(@NotNull String name, @NotNull String address)
	{
		this(name, address, false);
	}

	/**
	 * Constructor for initial construction (doesn't set the database key)
	 * 
	 * Use this constructor with care: setting "isVerified" to true should
	 * generally be an automated process.
	 */
	public EmailRecipient(@NotNull String name, @NotNull String address,
			boolean verified)
	{
		super(name);

		if (address == null)
			throw new IllegalArgumentException("Address can't be null");

		this.address = address;
		this.verified = false;
	}

	/**
	 * Constructor to for withProperty method chaining, cloning, and
	 * reconstitution
	 */
	public EmailRecipient(@NotNull String name, @NotNull String address,
			boolean verified, String dbKey)
	{
		super(name, dbKey);
		this.address = address;
		this.verified = verified;
	}

	@Override
	public EmailRecipient withName(String name)
	{
		return new EmailRecipient(name, getAddress(), isVerified(), getKey());
	}

	@Override
	public EmailRecipient withVerified(boolean verified)
	{
		return new EmailRecipient(getName(), getAddress(), verified, getKey());
	}

	@Override
	/**
	 * Clone the recipient--does *not* copy the dbKey.
	 */
	public Recipient clone()
	{
		EmailRecipient result = new EmailRecipient(getName(), getAddress(),
				isVerified());
		return result;
	}

	public EmailRecipient withAddress(String address)
	{
		return new EmailRecipient(getName(), address, isVerified(), getKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "EmailRecipient [name=" + getName() + ", address=" + address
				+ ", verified=" + verified + ", dbKey=" + getKey() + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		EmailRecipient other = (EmailRecipient) obj;
		if (address == null)
		{
			if (other.address != null) return false;
		}
		else if (!address.equals(other.address)) return false;
		return true;
	}
}
