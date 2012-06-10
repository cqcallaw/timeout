package net.brainvitamins.timeout.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ChannelServiceAsync
{
	void getChannelToken(String tag, AsyncCallback<String> callback);
}
