package net.brainvitamins.timeout.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("checkin")
public interface CheckinService extends RemoteService
{
	public void Checkin(long timeout);
}
