package net.brainvitamins.timeout.shared;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class Timeout extends Activity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5435541820544915944L;

	@Persistent(defaultFetchGroup = "true")
	private long timeout;

	public long getTimeout()
	{
		return timeout;
	}

	@Persistent(defaultFetchGroup = "true")
	private Date startTime;

	public Date getStartTime()
	{
		return startTime;
	}

	public Timeout()
	{
		this(new Date(), 0, new Date());
	}

	public Timeout(Date timestamp, long timeout, Date startTime)
	{
		super(timestamp);
		this.timeout = timeout;
		this.startTime = startTime;
	}
}
