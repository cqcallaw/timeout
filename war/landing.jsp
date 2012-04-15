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
<%@ page import="net.brainvitamins.presencepoll.server.Activity"%>
<%@ page import="net.brainvitamins.presencepoll.server.Checkin"%>
<%@ page import="net.brainvitamins.presencepoll.server.Timeout"%>

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
			List<Activity> activityLog = Constants.SERVICE.getActivityLog(user, 5);
	
			Object timeout = new Long(10000);
	
			if (activityLog.isEmpty())
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
				for (Activity entry : activityLog)
				{
					if (entry.getClass().equals(Checkin.class))
					{
						Checkin result = (Checkin)entry;
					%>
						<div class="checkin">
							[<%=result.getTimestamp().toString()%>] Checked in
			
							<%
							long entryTimeout = result.getTimeout();
							//take the timeout from the most recent checkin as the default for the next checkin timeout
							if (!timeoutSet)
							{
								timeout = entryTimeout;
								timeoutSet = true;
							}
							%>
	
							<%="for " + timeout.toString() + " milliseconds."%>
							<%
						%>
						</div>
						<%
					}
					else if (entry.getClass().equals(Timeout.class))
					{
						Timeout result = (Timeout)entry;
					%>
						<div class="timeout">
							[<%=result.getTimestamp().toString()%>] Checkin timed out.
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