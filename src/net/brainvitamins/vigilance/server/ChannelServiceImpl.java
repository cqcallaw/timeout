package net.brainvitamins.vigilance.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ChannelServiceImpl extends RemoteServiceServlet implements
		net.brainvitamins.vigilance.shared.services.ChannelService
{
	private static final Logger log = Logger.getLogger(ChannelServiceImpl.class
			.getName());

	private static final long serialVersionUID = -4802678016948213228L;

	@Override
	public String getChannelToken()
	{
		// TODO: maintain a list of constructed channels

		String clientId = getThreadLocalRequest().getSession().getId();

		log.log(Level.FINE, "Generated channel client id: " + clientId);

		ChannelService channelService = ChannelServiceFactory
				.getChannelService();

		String token = channelService.createChannel(clientId);

		log.log(Level.FINE, "Generated channel token: " + token);

		return token;
	}
}
