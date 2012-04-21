package net.brainvitamins.timeout.shared;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.mail.internet.InternetAddress;

@PersistenceCapable
public class EmailRecipient extends Recipient
{
	@Persistent
	private InternetAddress address;

	public InternetAddress getAddress()
	{
		return address;
	}

	public EmailRecipient(InternetAddress address)
	{
		this.address = address;
	}
}
