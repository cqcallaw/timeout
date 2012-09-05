package net.brainvitamins.timeout.server;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;
import net.brainvitamins.timeout.shared.Timeout;
import net.brainvitamins.timeout.shared.operations.CreateOperation;

/**
 * Notify the server of a timeout (this should only be done by an enqueued Task)
 */
public class TimeoutServlet extends HttpServlet
{
	private static final Logger logger = Logger.getLogger(TimeoutServlet.class
			.getName());

	private static final long serialVersionUID = -1807657555459402441L;

	// TODO: secure timeout URL (shouldn't be directly callable by the user)
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IllegalArgumentException
	{
		String userId = req.getParameter("userId");
		String startTimeParameter = req.getParameter("startTime");
		String timeoutParameter = req.getParameter("timeout");
		String sourceSessionId = req.getParameter("sourceSessionId");

		if (userId == null)
			throw new IllegalArgumentException(
					"Parameter userId cannot be null.");
		if (startTimeParameter == null)
			throw new IllegalArgumentException(
					"Parameter startTime cannot be null.");
		if (timeoutParameter == null)
			throw new IllegalArgumentException(
					"Parameter timeout cannot be null.");

		if (sourceSessionId == null)
			throw new IllegalArgumentException(
					"Parameter sourceSessionId cannot be null.");

		long timeout = Long.parseLong(timeoutParameter);

		Date startTime;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat(
					Constants.INTERNALDATEFORMAT);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			startTime = format.parse(startTimeParameter);

		}
		catch (ParseException e)
		{
			logger.log(Level.SEVERE, "Failed to parse timestamp: "
					+ startTimeParameter);
			return;
		}

		Timeout activity = new Timeout(new Date(), timeout, startTime);

		ActivityOperations.log(userId, activity);

		User user = UserOperations.getUserWithRecipients(userId);

		if (user == null)
			throw new IllegalArgumentException("Invalid user specified.");

		for (Recipient recipient : user.getRecipients())
		{
			if (recipient instanceof EmailRecipient)
			{
				EmailRecipient emailRecipient = (EmailRecipient) recipient;
				if (emailRecipient.isVerified())
				{
					try
					{
						sendNotification(emailRecipient, user, activity);
					}
					catch (UnsupportedEncodingException e)
					{
						System.out
								.println("Unsupported encoding exception while sending notification to recipient "
										+ emailRecipient.toString()
										+ ": "
										+ e.getMessage());
					}
					catch (MessagingException e)
					{
						System.out
								.println("Failed to send notification to recipient "
										+ emailRecipient.toString()
										+ ": "
										+ e.getMessage());
					}
				}
			}
			// TODO: handle non-email recipients
		}

		PushOperations.pushToListener(sourceSessionId,
				new CreateOperation<Activity>(activity));
	}

	private void sendNotification(EmailRecipient recipient, User user,
			Timeout timeout) throws UnsupportedEncodingException,
			MessagingException
	{
		MailOperations.sendMessage(
				"Vigilance Control timeout notification",
				"User " + user.getNickname() + " checked in at "
						+ timeout.getStartTime()
						+ ". This checkin timed out at "
						+ timeout.getTimestamp(), recipient.getName(),
				recipient.getAddress(), "Vigilance Control",
				"admin@vigilance-control.appspotmail.com");
	}
}