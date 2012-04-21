package net.brainvitamins.timeout.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User
{
	@PrimaryKey
	private String id;

	private String nickname;

	@Persistent
	private List<Activity> activityLog;

	@Persistent
	private Set<Recipient> recipients;

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return id;
	}

	public String getNickname()
	{
		return nickname;
	}

	/**
	 * @return the activityLog
	 */
	public List<Activity> getActivityLog()
	{
		return activityLog;
	}

	/**
	 * @return the recipients
	 */
	public Set<Recipient> getRecipients()
	{
		return recipients;
	}

	public User(String userId, String nickname)
	{
		super();
		this.id = userId;
		this.nickname = nickname;
		activityLog = new ArrayList<Activity>();
		recipients = new HashSet<Recipient>();
	}
}
