package net.brainvitamins.timeout.client.services;

import java.util.List;

import net.brainvitamins.timeout.shared.Recipient;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RecipientServiceAsync
{
	void addRecipient(Recipient recipient, AsyncCallback<Void> callback);

	void getRecipients(AsyncCallback<List<Recipient>> callback);

	void removeRecipient(Recipient recipient, AsyncCallback<Boolean> callback);
}
