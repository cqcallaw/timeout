package net.brainvitamins.timeout.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;
import net.brainvitamins.timeout.shared.Timeout;
import net.brainvitamins.timeout.shared.operations.CreateOperation;
import net.brainvitamins.timeout.shared.operations.DeleteOperation;
import net.brainvitamins.timeout.shared.operations.UpdateOperation;
import net.brainvitamins.timeout.shared.services.RecipientService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RecipientServiceImpl extends RemoteServiceServlet implements
		RecipientService
{
	private static final long serialVersionUID = 6581374344337957491L;

	@Override
	public void addRecipient(@NotNull Recipient recipient)
			throws IllegalArgumentException
	{
		validateRecipient(recipient);

		addRecipientCore(recipient);
	}

	@Override
	public void addRecipient(@NotNull EmailRecipient recipient)
			throws IllegalArgumentException, UnsupportedEncodingException
	{
		validateRecipient(recipient);

		// TODO: handle email failure modes:
		// -delivery fails
		// -delivery fails remotely (initial send is a success)
		// -delivery succeeds but adding the recipient to the app engine
		// database fails

		addRecipientCore(recipient);

		requestConfirmation(recipient, Utilities.getCurrentUser());
	}

	/**
	 * @param recipient
	 */
	private void addRecipientCore(@NotNull Recipient recipient)
	{
		RecipientOperations.addRecipient(recipient,
				Utilities.getCurrentUserHashedId());
		RecipientOperations.pushToClient(getThreadLocalRequest().getSession()
				.getId(), new CreateOperation<Recipient>(recipient));
	}

	@Override
	public List<Recipient> getRecipients()
	{
		List<Recipient> result = new ArrayList<Recipient>(Utilities
				.getCurrentUserWithRecipients().getRecipients());

		// sort by name, descending
		Collections.sort(result, new Comparator<Recipient>()
		{
			@Override
			public int compare(Recipient o1, Recipient o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		return result;
	}

	@Override
	public void updateRecipient(@NotNull Recipient recipient)
			throws IllegalArgumentException
	{
		validateRecipient(recipient);

		updateRecipientCore(recipient);
	}

	@Override
	public void updateRecipient(@NotNull EmailRecipient recipient)
			throws IllegalArgumentException
	{
		validateRecipient(recipient);

		updateRecipientCore(recipient);
	}

	/**
	 * @param recipient
	 */
	private void updateRecipientCore(Recipient recipient)
	{
		RecipientOperations.updateRecipient(recipient,
				Utilities.getCurrentUserHashedId());
		RecipientOperations.pushToClient(getThreadLocalRequest().getSession()
				.getId(), new UpdateOperation<Recipient>(recipient));
	}

	@Override
	public void removeRecipient(@NotNull Recipient recipient)
			throws IllegalArgumentException
	{
		validateRecipient(recipient);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withRecipients");

		try
		{
			User currentUser = pm.getObjectById(User.class,
					Utilities.getCurrentUserHashedId());

			Recipient ref = RecipientOperations.getDatabaseReference(recipient,
					currentUser.getRecipients());

			if (ref == null)
				throw new IllegalArgumentException(
						"Recipient not found in database");
			else
				currentUser.getRecipients().remove(ref);
		}
		finally
		{
			pm.close();
		}

		RecipientOperations.pushToClient(getThreadLocalRequest().getSession()
				.getId(), new DeleteOperation<Recipient>(recipient));
	}

	/**
	 * @param recipient
	 */
	private void validateRecipient(Recipient recipient)
	{
		// validation
		if (recipient == null)
			throw new IllegalArgumentException("Recipient cannot be null.");

		if (recipient.getName() == null)
			throw new IllegalArgumentException("Name cannot be null.");

		if (recipient.getName().equals(""))
			throw new IllegalArgumentException("Name cannot be empty.");
	}

	/**
	 * @param recipient
	 */
	private void validateRecipient(EmailRecipient recipient)
	{
		validateRecipient((Recipient) recipient);

		try
		{
			new InternetAddress(recipient.getAddress());
		}
		catch (AddressException e)
		{
			throw new IllegalArgumentException("Invalid email address.");
		}
	}

	private void sendNotification(EmailRecipient recipient, User user,
			Timeout timeout) throws UnsupportedEncodingException,
			MessagingException
	{
		MailOperations
				.sendMessage(
						"Timeout notification",
						"User " + user.getNickname() + "checked in at "
								+ timeout.getStartTime()
								+ ". This checkin timed out at "
								+ timeout.getTimestamp(), recipient.getName(),
						recipient.getAddress(), "The Admin",
						"admin@---appspotmail.com");
	}

	/*
	 * ref: http://stackoverflow.com/a/415971/577298
	 * http://stackoverflow.com/a/10604659/577298
	 */
	private void requestConfirmation(EmailRecipient recipient, User user)
			throws UnsupportedEncodingException
	{
		// sanity check: the recipient needs an assigned data key before we send
		// out a confirmation request
		if (recipient.getKey() == null)
			throw new IllegalStateException(
					"Recipient must have a non-null database key before confirmation can be requested.");

		HttpServletRequest request = this.getThreadLocalRequest();

		String verificationURL = "http://" + request.getServerName() + ":"
				+ request.getServerPort() + "/timeout/verification?userId="
				+ user.getId() + "&recipientId=" + recipient.getKey();
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
}
