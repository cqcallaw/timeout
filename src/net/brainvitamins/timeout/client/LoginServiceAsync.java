package net.brainvitamins.timeout.client;

import net.brainvitamins.timeout.shared.LoginInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync
{
	void login(String requestUri, AsyncCallback<LoginInfo> callback);
}