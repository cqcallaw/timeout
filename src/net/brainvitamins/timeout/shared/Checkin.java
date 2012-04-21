package net.brainvitamins.timeout.shared;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
//@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class Checkin extends Activity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3016832144903168986L;

	@Persistent(defaultFetchGroup = "true")
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
