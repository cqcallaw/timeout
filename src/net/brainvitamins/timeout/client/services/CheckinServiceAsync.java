package net.brainvitamins.timeout.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CheckinServiceAsync
{
	void Checkin(long timeout, AsyncCallback<Void> callback);
}