package net.brainvitamins.vigilance.shared.services;

import net.brainvitamins.vigilance.shared.LoginInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService
{
	public LoginInfo login(String requestUri);
}