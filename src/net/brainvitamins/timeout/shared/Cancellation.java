package net.brainvitamins.timeout.shared;

import java.io.Serializable;

public class Cancellation extends Activity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3820185349208627784L;

	private Checkin subject;

	public Checkin getSubject()
	{
		return subject;
	}

	public Cancellation(Checkin subject)
	{
		this.subject = subject;
	}
}
