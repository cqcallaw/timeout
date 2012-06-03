package net.brainvitamins.timeout.server;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailOperations
{
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
		Transport.send(msg);
	}
}
