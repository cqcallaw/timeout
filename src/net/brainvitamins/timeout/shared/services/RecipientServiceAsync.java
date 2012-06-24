package net.brainvitamins.timeout.shared.services;

import java.util.List;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RecipientServiceAsync
{
	void addRecipient(Recipient recipient, AsyncCallback<Void> callback);

	void addRecipient(EmailRecipient recipient, AsyncCallback<Void> callback);

	void updateRecipient(EmailRecipient recipient, AsyncCallback<Void> callback);

	void getRecipients(AsyncCallback<List<Recipient>> callback);

	void removeRecipient(Recipient recipient, AsyncCallback<Void> callback);
}
