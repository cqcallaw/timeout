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

	public EmailRecipient()
	{
		this("", "");
	}

	public EmailRecipient(@NotNull String name, @NotNull String address)
	{
		super(name, false);

		if (address == null)
			throw new IllegalArgumentException("Address can't be null");

		this.address = address;
	}

	/*
	 * Protected constructor for clone operations (doesn't set the database keys)
	 */
	protected EmailRecipient(@NotNull String name, boolean verified,
			@NotNull String address)
	{
		super(name, verified);
		this.address = address;
	}

	/*
	 * Protected constructor to enable withProperty method chaining
	 */
	protected EmailRecipient(@NotNull String name, boolean verified,
			@NotNull String address, String dbKey)
	{
		super(name, verified, dbKey);
		this.address = address;
	}

	@Override
	public EmailRecipient withName(String name)
	{
		return new EmailRecipient(name, isVerified(), getAddress(), getKey());
	}

	@Override
	public EmailRecipient withVerified(boolean verified)
	{
		return new EmailRecipient(getName(), verified, getAddress(), getKey());
	}

	@Override
	public Recipient clone()
	{
		return new EmailRecipient(getName(), isVerified(), getAddress());
	}

	public EmailRecipient withAddress(String address)
	{
		return new EmailRecipient(getName(), isVerified(), address, getKey());
	}

	@Override
	public String toString()
	{
		return getName() + ":" + getAddress()
				+ (isVerified() ? "(Verified)" : "(Unverified)");
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
