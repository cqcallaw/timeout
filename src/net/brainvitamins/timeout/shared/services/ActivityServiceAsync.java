package net.brainvitamins.timeout.shared.services;

import java.util.List;

import net.brainvitamins.timeout.shared.Activity;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ActivityServiceAsync
{
	void getActivityLog(int sizeLimit, AsyncCallback<List<Activity>> callback);

	void checkin(long timeout, AsyncCallback<Void> callback);

	void cancelCheckin(AsyncCallback<Void> callback);
}
