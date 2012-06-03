package net.brainvitamins.timeout.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ChannelServiceAsync
{
	void getActivityChannelToken(AsyncCallback<String> callback);

	void getRecipientChannelToken(AsyncCallback<String> callback);
}
