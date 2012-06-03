package net.brainvitamins.timeout.server;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ChannelServiceImpl extends RemoteServiceServlet implements
		net.brainvitamins.timeout.shared.services.ChannelService
{
	private static final long serialVersionUID = -4802678016948213228L;

	@Override
	public String getActivityChannelToken()
	{
		// TODO: maintain a list of constructed channels

		String clientId = getThreadLocalRequest().getSession().getId()
				+ ":activity";

		System.out.println("Channel client id: " + clientId);

		ChannelService channelService = ChannelServiceFactory
				.getChannelService();

		String token = channelService.createChannel(clientId);

		System.out.println("Activity channel token: " + token);

		return token;
	}

	@Override
	public String getRecipientChannelToken()
	{
//		String clientId = getThreadLocalRequest().getSession().getId()
//				+ ":recipient";
//
//		System.out.println("Channel client id: " + clientId);
//
//		ChannelService channelService = ChannelServiceFactory
//				.getChannelService();
//
//		String token = channelService.createChannel(clientId);
//
//		System.out.println("Recipient channel token: " + token);
//
//		return token;
		return "recipient-channel";
	}
}
