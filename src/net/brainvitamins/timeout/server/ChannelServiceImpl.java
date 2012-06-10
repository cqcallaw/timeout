package net.brainvitamins.timeout.server;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ChannelServiceImpl extends RemoteServiceServlet implements
		net.brainvitamins.timeout.shared.services.ChannelService
{
	private static final long serialVersionUID = -4802678016948213228L;

	@Override
	public String getChannelToken(String tag)
	{
		// TODO: maintain a list of constructed channels

		String clientId = getThreadLocalRequest().getSession().getId() + ":" + tag;

		System.out.println("Generated channel client id: " + clientId);

		ChannelService channelService = ChannelServiceFactory
				.getChannelService();

		String token = channelService.createChannel(clientId);

		System.out.println("Generated channel token: " + token);

		return token;
	}
}
