package net.brainvitamins.timeout.client;

import java.util.List;

import net.brainvitamins.timeout.shared.Activity;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ActivityServiceAsync
{
	void getActivityLog(int sizeLimit, AsyncCallback<List<Activity>> callback);
}
