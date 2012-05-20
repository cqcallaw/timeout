package net.brainvitamins.timeout.shared.services;


import java.util.List;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("recipient")
public interface RecipientService extends RemoteService
{
	public void saveRecipient(Recipient recipient) throws IllegalArgumentException;
	
	public void saveRecipient(EmailRecipient recipient) throws IllegalArgumentException;

	public boolean removeRecipient(Recipient recipient);

	public List<Recipient> getRecipients();
}
