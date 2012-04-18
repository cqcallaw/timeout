package net.brainvitamins.timeout.server;

import java.util.Date;

import net.brainvitamins.timeout.shared.Activity;

import com.google.appengine.api.users.User;

public class Checkin extends Activity
{
	private User user;
	public User getUser()
	{
		return user;
	}

	private long timeout;
	public long getTimeout()
	{
		return timeout;
	}

	public Checkin(User user, Date timestamp, long timeout)
	{
		super(timestamp);
		this.user = user;
		this.timeout = timeout;
	}
}
