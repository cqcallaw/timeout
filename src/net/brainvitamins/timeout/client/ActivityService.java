package net.brainvitamins.timeout.client;

import java.util.List;

import net.brainvitamins.timeout.shared.Activity;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("activity")
public interface ActivityService extends RemoteService
{
	public List<Activity> getActivityLog(int sizeLimit);
}
