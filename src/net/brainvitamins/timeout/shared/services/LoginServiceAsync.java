package net.brainvitamins.timeout.shared.services;

import net.brainvitamins.timeout.shared.LoginInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync
{
	void login(String requestUri, AsyncCallback<LoginInfo> callback);
}
