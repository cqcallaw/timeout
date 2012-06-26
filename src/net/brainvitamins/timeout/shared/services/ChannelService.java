package net.brainvitamins.timeout.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("channel")
public interface ChannelService extends RemoteService
{
	public String getChannelToken();
}
