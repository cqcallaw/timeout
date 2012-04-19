package net.brainvitamins.timeout.shared;

import java.io.Serializable;
import java.util.Date;

public class Checkin extends Activity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3016832144903168986L;

	private long timeout;

	public long getTimeout()
	{
		return timeout;
	}

	public Checkin()
	{
		this(new Date(), 0);
	}

	public Checkin(Date timestamp, long timeout)
	{
		super(timestamp);
		this.timeout = timeout;
	}
}
