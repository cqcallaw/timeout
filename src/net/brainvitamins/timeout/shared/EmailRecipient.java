package net.brainvitamins.timeout.shared;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

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

	public EmailRecipient(String name, boolean verified)
	{
		this(name, verified, "");
	}

	public EmailRecipient(String name, boolean verified, String address)
	{
		super(name, verified);
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
