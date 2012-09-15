package net.brainvitamins.vigilance.shared.services;

import java.io.UnsupportedEncodingException;
import java.util.List;

import net.brainvitamins.vigilance.shared.EmailRecipient;
import net.brainvitamins.vigilance.shared.Recipient;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("recipient")
public interface RecipientService extends RemoteService
{
	public void addRecipient(Recipient recipient)
			throws IllegalArgumentException;

	public void addRecipient(EmailRecipient recipient)
			throws IllegalArgumentException, UnsupportedEncodingException;

	public void updateRecipient(EmailRecipient recipient)
			throws IllegalArgumentException, UnsupportedEncodingException;

	public void removeRecipient(Recipient recipient)
			throws IllegalArgumentException;

	public List<Recipient> getRecipients();
}
