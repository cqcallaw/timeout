package net.brainvitamins.timeout.shared;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.gson.annotations.Expose;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

@PersistenceCapable
public class Timeout extends Activity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5435541820544915944L;

	@Expose
	@Persistent(defaultFetchGroup = "true")
	private long timeout;

	public long getTimeout()
	{
		return timeout;
	}

	@Expose
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Timeout [timestamp=" + getTimestamp().toGMTString()
				+ ", timeout=" + timeout + ", startTime="
				+ startTime.toGMTString() + "]";
	}
}
