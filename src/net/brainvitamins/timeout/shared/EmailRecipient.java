package net.brainvitamins.timeout.shared;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.validation.constraints.NotNull;

@PersistenceCapable
public class EmailRecipient extends Recipient implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2341504040837658628L;

	@Persistent
	private String address;

	public String getAddress()
	{
		return address;
	}

	public EmailRecipient()
	{
		this("", false, "");
	}

	public EmailRecipient(@NotNull String name,
			boolean verified)
	{
		this(name, verified, "");
	}

	public EmailRecipient(@NotNull String name,
			boolean verified, @NotNull String address)
	{
		super(name, verified);

		if (address == null)
			throw new IllegalArgumentException("Address can't be null");

		this.address = address;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof EmailRecipient)
		{
			EmailRecipient that = (EmailRecipient) other;
			return this.getName().equals(that.getName())
					&& this.address.equals(that.getAddress());
		}
		return false;
	}

	@Override
	public String toString()
	{
		return getName() + ":" + getAddress()
				+ (isVerified() ? "(Verified)" : "(Unverified)");
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode() ^ getAddress().hashCode();
	}
}
