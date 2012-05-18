package net.brainvitamins.timeout.shared.services;

import java.util.List;

import net.brainvitamins.timeout.shared.Activity;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("activity")
public interface ActivityService extends RemoteService
{
	public void checkin(long timeout);

	public void cancelCheckin() throws IllegalStateException;

	public List<Activity> getActivityLog(int sizeLimit);
}
