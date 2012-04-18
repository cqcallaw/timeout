package net.brainvitamins.timeout.shared;

import java.io.Serializable;
import java.util.Date;

public class Checkin extends Activity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3016832144903168986L;

	private String userId;

	public String getUser()
	{
		return userId;
	}

	private long timeout;

	public long getTimeout()
	{
		return timeout;
	}

	public Checkin()
	{
		this("", new Date(), 0);
	}

	public Checkin(String userId, Date timestamp, long timeout)
	{
		super(timestamp);
		this.userId = userId;
		this.timeout = timeout;
	}
}
