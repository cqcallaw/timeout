package net.brainvitamins.timeout.shared;

import java.io.Serializable;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.validation.constraints.NotNull;

@PersistenceCapable
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public abstract class Recipient implements Serializable
{
	private static final long serialVersionUID = 8775141775408581192L;

	@SuppressWarnings("unused")
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String key;

	// TODO: think of how this could be re-implemented as an enumeration
	@Persistent
	private boolean verified;

	public boolean isVerified()
	{
		return verified;
	}

	@Persistent
	private String name;

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	public Recipient()
	{
		this("", false);
	}

	public Recipient(@NotNull String name)
	{
		this(name, false);
	}

	public Recipient(@NotNull String name, boolean verified)
	{
		if (name == null)
			throw new IllegalArgumentException("Name can't be null");

		this.verified = verified;
		this.name = name;
	}
}
