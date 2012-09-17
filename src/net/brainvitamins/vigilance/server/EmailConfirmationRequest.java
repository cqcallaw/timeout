package net.brainvitamins.vigilance.server;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import net.brainvitamins.vigilance.shared.EmailRecipient;

@PersistenceCapable
public class EmailConfirmationRequest extends
		ConfirmationRequest<EmailRecipient>
{
	@Persistent
	private String unverifiedAddress;

	public String getUnverifiedAddress()
	{
		return unverifiedAddress;
	}

	public EmailConfirmationRequest(EmailRecipient recipient)
	{
		super(recipient);

		unverifiedAddress = recipient.getAddress();
	}
}
