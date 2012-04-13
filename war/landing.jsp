<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page
	import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="net.brainvitamins.presencepoll.server.CheckinServlet"%>
<%@ page import="net.brainvitamins.presencepoll.server.Constants"%>

<html>
<head>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
</head>
<body>
<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null)
		{
			%>
			<p class="salute">
				Welcome, <%=user.getNickname()%>!<br />
				<a href="<%=userService.createLogoutURL(request.getRequestURI())%>">sign out</a>
			</p>
	
			<%
			DatastoreService datastore = DatastoreServiceFactory
						.getDatastoreService();
			
			Key activityStoreKey = KeyFactory.createKey(Constants.activityKindIdentifier, user.getUserId().toString());
			
			Query query = new Query(Constants.activityKindIdentifier, activityStoreKey)
					.addSort("time", Query.SortDirection.DESCENDING);
			
			List<Entity> activity = datastore.prepare(query).asList(
					FetchOptions.Builder.withLimit(5));
	
			Object timeout = new Long(10000);
	
			if (activity == null || activity.isEmpty())
			{
			%>
				<p>No recent activity.</p>
			<%
			}
			else
			{
				boolean timeoutSet = false;
			%>
				<h1>Recent Activity</h1>
				<div id="activity">
				<%
				for (Entity entry : activity)
				{
					if (entry.getProperty("type").equals("checkin"))
					{
					%>
						<div class="checkin">
							[<%=entry.getProperty("time").toString()%>] Checked in
			
							<%
							Object entryTimeout = entry.getProperty("timeout");
							if (entryTimeout != null)
							{
								//take the timeout from the most recent checkin as the default for the next checkin timeout
								if (!timeoutSet)
								{
									timeout = entryTimeout;
									timeoutSet = true;
								}
								%>
		
								<%="for " + timeout.toString()
																+ " milliseconds."%>
							<%
							}
						%>
						</div>
						<%
					}
					else if (entry.getProperty("type").equals("timeout"))
					{
					%>
						<div class="timeout">
							[<%=entry.getProperty("time").toString()%>] Checkin timed out.
						</div>
					<%
					}
				}
			}
			%>
			</div>
			
			<div id="start-checkin">
				<form action="/presence_poll/checkin" method="post">
					<div>
						<input type="submit" value="Checkin" /> for <input type="text"
							name="timeout" value="<%=timeout.toString()%>" /> milliseconds.
					</div>
				</form>
			</div>
			<h1>Notified</h1>
			<div>
			</div>
	<% 
	} else { %>
		<p>
			Hello! Please <a href="<%=userService.createLoginURL(request.getRequestURI())%>">Sign in</a>.
		</p>
	<%
	}
%>

</body>
</html>