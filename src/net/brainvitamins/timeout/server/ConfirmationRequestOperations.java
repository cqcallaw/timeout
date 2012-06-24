package net.brainvitamins.timeout.server;

import java.io.UnsupportedEncodingException;

import javax.jdo.PersistenceManager;
import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;

import net.brainvitamins.timeout.shared.EmailRecipient;

public class ConfirmationRequestOperations
{
	/**
	 * send out an email confirmation request (not sure this is a good
	 * abstraction yet...)
	 * 
	 * @param recipient
	 * @param user
	 * @param confirmationURLBase
	 * @throws UnsupportedEncodingException
	 */
	public static void sendConfirmationRequest(
			@NotNull EmailRecipient recipient, @NotNull User user,
			@NotNull String confirmationURLBase) throws UnsupportedEncodingException
	{
		// sanity checks
		if (recipient == null)
		{
			throw new IllegalArgumentException("recipient cannot be null.");
		}

		if (user == null)
		{
			throw new IllegalArgumentException("user cannot be null.");
		}

		if (recipient.getKey() == null)
			throw new IllegalStateException(
					"Recipient must have a non-null database key before confirmation can be requested.");

		ConfirmationRequest<EmailRecipient> confirmationRequest = ConfirmationRequestOperations
				.getConfirmationRequest(user, recipient);

		String verificationURL = confirmationURLBase + "?confirmationId="
				+ confirmationRequest.getId();
		System.out.println("Confirmation Request URL: " + verificationURL);

		try
		{
			MailOperations
					.sendMessage(
							"Recipient confirmation",
							"User has added you as a recipient of timeout notifications",
							recipient.getName(), recipient.getAddress(),
							"The Admin", "admin@---appspotmail.com");
		}
		catch (MessagingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Generate a confirmation, persist it to the datastore, and return it
	 * 
	 * @param user
	 * @param recipient
	 * 
	 * @return
	 */
	public static ConfirmationRequest<EmailRecipient> getConfirmationRequest(
			User user, EmailRecipient recipient)
	{
		if (recipient == null)
			throw new IllegalArgumentException("recipient cannot be null.");

		if (recipient.getKey() == null)
			throw new IllegalArgumentException("recipient key must be null.");

		ConfirmationRequest<EmailRecipient> confirmationRequest = new ConfirmationRequest<EmailRecipient>(
				recipient);

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			pm.makePersistent(confirmationRequest);
		}
		finally
		{
			pm.close();
		}

		return confirmationRequest;
	}
}
