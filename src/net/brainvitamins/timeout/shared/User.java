package net.brainvitamins.timeout.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.FetchGroups;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
@FetchGroups({
		@FetchGroup(name = "withRecipients", members = { @Persistent(name = "recipients") }),
		@FetchGroup(name = "withActivityLog", members = { @Persistent(name = "activityLog") }) })
public class User
{
	@PrimaryKey
	private String id;

	private String nickname;

	@Persistent(dependentElement = "true")
	private List<Activity> activityLog;

	@Persistent(dependentElement = "true")
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
		// this data model does not share recipients between users.
		// this may prove inefficient at scale.
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
