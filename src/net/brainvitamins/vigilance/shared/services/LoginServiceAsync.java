package net.brainvitamins.vigilance.shared.services;

import net.brainvitamins.vigilance.shared.LoginInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync
{
	void login(String requestUri, AsyncCallback<LoginInfo> callback);
}
