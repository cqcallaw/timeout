package net.brainvitamins.timeout.client;

import net.brainvitamins.timeout.shared.EmailRecipient;

/*
 * Mutable proxy class for EmailRecipients, for Editor support
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
		result = new EmailRecipient(name, result.isVerified(),
				result.getAddress());
	}

	public String getAddress()
	{
		return result.getName();
	}

	public void setAddress(String address)
	{
		result = new EmailRecipient(result.getName(), result.isVerified(),
				address);
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
