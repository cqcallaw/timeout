package net.brainvitamins.timeout.client.editors;

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
		// TODO: strip any markup out, for safety
		if (!(name == null))
			result = new EmailRecipient(name, result.isVerified(),
					result.getAddress());
	}

	public String getAddress()
	{
		return result.getName();
	}

	public void setAddress(String address)
	{
		if (!(address == null))
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
