package net.brainvitamins.timeout.server;

import javax.jdo.PersistenceManager;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.operations.CreateOperation;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class ActivityOperations
{
	public static void log(String userId, Activity activity)
	{
		System.out.println("Logging " + activity.toString());
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withActivityLog");
		try
		{
			User currentUser = pm.getObjectById(User.class, userId);
			currentUser.getActivityLog().add(activity);
		}
		finally
		{
			pm.close();
		}
	}

	// TODO: multi-session notifications
	/**
	 * Send an activity notification to the client over a Channel.
	 * 
	 * @param sessionId
	 * @param activity
	 */
	public static void pushToClient(String sessionId, Activity activity)
	{
		ChannelService channelService = ChannelServiceFactory
				.getChannelService();

		String channelKey = sessionId + ":activity";

		channelService.sendMessage(new ChannelMessage(channelKey,
				new CreateOperation<Activity>(activity).toString()));
	}
}
