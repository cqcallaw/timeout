package net.brainvitamins.timeout.shared;

import java.io.Serializable;
import java.util.Date;

//TODO: investigate why this caused problems when it wasn't serializable
public abstract class Activity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3009614156402312511L;
	
	private Date timestamp;

	public Date getTimestamp()
	{
		return timestamp;
	}

	public Activity()
	{
		timestamp = new Date();
	}
	
	public Activity(Date timestamp)
	{
		if (timestamp == null)
			throw new IllegalArgumentException("Timestamp cannot be null.");

		this.timestamp = timestamp;
	}
}
