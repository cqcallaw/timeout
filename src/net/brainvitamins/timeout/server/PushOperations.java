package net.brainvitamins.timeout.server;

import net.brainvitamins.timeout.shared.operations.DataOperation;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class PushOperations
{
	// TODO: multi-session notifications
	/**
	 * Push a data operation to the client.
	 * 
	 * @param sessionId
	 * @param activity
	 */
	public static <T> void pushToListener(String sessionId,
			DataOperation<T> operation)
	{
		ChannelService channelService = ChannelServiceFactory
				.getChannelService();

		channelService.sendMessage(new ChannelMessage(sessionId, operation
				.toString()));
	}
}
