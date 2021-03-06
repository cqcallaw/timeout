package net.brainvitamins.vigilance.server;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailOperations
{
	private static final Logger logger = Logger.getLogger(MailOperations.class
			.getName());

	/**
	 * 
	 * @param subject
	 * @param body
	 * @param name
	 * @param address
	 * @param fromName
	 * @param fromAddress
	 * @throws UnsupportedEncodingException
	 *             if the fromName or fromAddress parameters are invalid
	 * @throws MessagingException
	 */
	public static void sendMessage(String subject, String body, String name,
			String address, String fromName, String fromAddress)
			throws UnsupportedEncodingException, MessagingException
	{
		Properties properties = new Properties();
		Session session = Session.getDefaultInstance(properties, null);

		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(fromAddress, fromName));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(address,
				name));
		msg.setSubject(subject);
		msg.setText(body);

		logger.log(Level.FINEST, "Sending message from " + fromAddress + " to "
				+ address);
		Transport.send(msg);
		logger.log(Level.FINEST, "Message sent.");
	}
}
