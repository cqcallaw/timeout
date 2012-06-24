package net.brainvitamins.timeout.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.FetchGroups;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Recipient;

@PersistenceCapable(detachable = "true")
@FetchGroups({
		@FetchGroup(name = "withRecipients", members = { @Persistent(name = "recipients") }),
		@FetchGroup(name = "withActivityLog", members = { @Persistent(name = "activityLog") }) })
public class User
{
	@PrimaryKey
	private String id;

	public String getId()
	{
		return id;
	}

	private String nickname;

	@Persistent(dependentElement = "true")
	@Order(extensions = @Extension(vendorName = "datanucleus", key = "list-ordering", value = "timestamp desc"))
	private List<Activity> activityLog;

	@Persistent(dependentElement = "true")
	@Order(extensions = @Extension(vendorName = "datanucleus", key = "list-ordering", value = "name asc"))
	private Set<Recipient> recipients;

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
		// this may not scale up well.
		return recipients;
	}

	/**
	 * 
	 * @param userId
	 *            the GAE userId
	 * @param nickname
	 */
	public User(String userId, String nickname)
	{
		super();
		this.id = UserOperations.hashUserId(userId);
		this.nickname = nickname;
		activityLog = new ArrayList<Activity>();
		recipients = new HashSet<Recipient>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "User [id=" + id + ", nickname=" + nickname + "]";
	}
}
