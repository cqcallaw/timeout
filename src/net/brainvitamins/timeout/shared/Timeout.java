package net.brainvitamins.timeout.shared;

import java.io.Serializable;
import java.util.Date;

public class Timeout extends Activity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5435541820544915944L;

	private String userEmail;

	public String getUserEmail()
	{
		return userEmail;
	}

	private long timeout;

	public long getTimeout()
	{
		return timeout;
	}

	private Date startTime;

	public Date getStartTime()
	{
		return startTime;
	}

	public Timeout()
	{
		this(new Date(), 0, new Date(), "", "");
	}

	public Timeout(Date timestamp, long timeout, Date startTime, String userId,
			String userEmail)
	{
		super(timestamp);
		this.timeout = timeout;
		this.startTime = startTime;
		this.userEmail = userEmail;
	}
}
