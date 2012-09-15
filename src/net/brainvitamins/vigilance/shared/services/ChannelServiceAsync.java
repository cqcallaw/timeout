package net.brainvitamins.vigilance.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ChannelServiceAsync
{
	void getChannelToken(AsyncCallback<String> callback);
}
