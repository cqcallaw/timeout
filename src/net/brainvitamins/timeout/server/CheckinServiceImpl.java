package net.brainvitamins.timeout.server;

import java.util.Date;

import net.brainvitamins.timeout.client.CheckinService;
import net.brainvitamins.timeout.shared.Checkin;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CheckinServiceImpl extends RemoteServiceServlet implements
		CheckinService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6878438653024198258L;

	@Override
	public void Checkin(long timeout)
	{
		Date timestamp = new Date();

		if (timeout < 1)
			throw new IllegalArgumentException(
					"Parameter timeout cannot be less than one (was it defined?)");

		Constants.ACTIVITYSERVICE.logActivity(new Checkin(timestamp, timeout));
	}
}
