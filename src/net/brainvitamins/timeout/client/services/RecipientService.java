package net.brainvitamins.timeout.client.services;


import java.util.List;

import net.brainvitamins.timeout.shared.Recipient;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("recipient")
public interface RecipientService extends RemoteService
{
	public void addRecipient(Recipient recipient);

	public boolean removeRecipient(Recipient recipient);

	public List<Recipient> getRecipients();
}
