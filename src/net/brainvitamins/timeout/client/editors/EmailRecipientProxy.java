package net.brainvitamins.timeout.client.editors;

import net.brainvitamins.timeout.shared.EmailRecipient;

/*
 * Mutable proxy class for EmailRecipients, so we can support immutable recipients and Editors simultaneously
 */
public class EmailRecipientProxy
{
	private EmailRecipient result;

	/**
	 * @return the result
	 */
	public EmailRecipient getResult()
	{
		return result;
	}

	public String getName()
	{
		return result.getName();
	}

	public void setName(String name)
	{
		// TODO: strip any markup out, for safety
		if (!(name == null)) result = result.withName(name);
	}

	public String getAddress()
	{
		return result.getAddress();
	}

	public void setAddress(String address)
	{
		if (!(address == null || address.equals(result.getAddress())))
			result = result.withAddress(address);
	}

	public EmailRecipientProxy()
	{
		this(new EmailRecipient());
	}

	public EmailRecipientProxy(EmailRecipient result)
	{
		this.result = result;
	}
}
