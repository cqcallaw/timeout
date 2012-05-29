package net.brainvitamins.timeout.shared;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class Cancellation extends Activity implements Serializable
{
	private static final long serialVersionUID = -3820185349208627784L;

	// ref:
	// http://groups.google.com/group/google-appengine-java/browse_thread/thread/f09cf3f98308cb91
	public Cancellation()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Cancellation [timestamp=" + getTimestamp() + "]";
	}
}
