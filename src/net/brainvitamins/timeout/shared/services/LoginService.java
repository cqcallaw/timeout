package net.brainvitamins.timeout.shared.services;

import net.brainvitamins.timeout.shared.LoginInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService
{
	public LoginInfo login(String requestUri);
}