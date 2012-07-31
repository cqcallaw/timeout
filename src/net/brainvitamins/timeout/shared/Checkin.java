package net.brainvitamins.timeout.shared;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable = "true")
public class Checkin extends Activity implements Serializable
{
	private static final long serialVersionUID = -3016832144903168986L;

	@Persistent
	private long timeout;

	public long getTimeout()
	{
		return timeout;
	}

	public Checkin()
	{
		this(new Date(), 1);
	}

	public Checkin(Date timestamp, long timeout)
	{
		super(timestamp);

		if (timeout < 1)
			throw new IllegalArgumentException(
					"Timeout cannot be less than one");

		this.timeout = timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public String toString()
	{
		return "Checkin [timestamp=" + getTimestamp().toGMTString()
				+ ", timeout=" + timeout + "]";
	}
}
