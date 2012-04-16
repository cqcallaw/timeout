package net.brainvitamins.timeout.server;

import java.util.Date;

public class Timeout extends Activity
{
	private String userId;

	public String getUserId()
	{
		return userId;
	}

	private String userEmail;

	public String getUserEmail()
	{
		return userEmail;
	}

	private Date timestamp;

	public Date getTimestamp()
	{
		return timestamp;
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

	public Timeout(Date timestamp, long timeout, Date startTime,
			String userId, String userEmail)
	{
		this.timestamp = timestamp;
		this.timeout = timeout;
		this.startTime = startTime;
		this.userId = userId;
		this.userEmail = userEmail;
	}
}
