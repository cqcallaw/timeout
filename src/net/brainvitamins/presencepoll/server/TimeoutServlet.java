package net.brainvitamins.presencepoll.server;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class TimeoutServlet extends HttpServlet
{
	private static final long serialVersionUID = -1807657555459402441L;

	// TODO: secure timeout URL (shouldn't be directly callable by the user)
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException
	{
		SimpleDateFormat format = new SimpleDateFormat(Constants.DATEFORMAT);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));

		String userId = req.getParameter("userId");
		String userEmail = req.getParameter("userEmail");
		String startTime = req.getParameter("startTime");
		String timeoutParameter = req.getParameter("timeout");

		if (userId == null)
			throw new IllegalArgumentException(
					"Parameter userId cannot be null.");
		if (startTime == null)
			throw new IllegalArgumentException(
					"Parameter startTime cannot be null.");
		if (timeoutParameter == null)
			throw new IllegalArgumentException(
					"Parameter timeout cannot be null.");

		Key activityStoreKey = KeyFactory.createKey(
				Constants.activityKindIdentifier, userId);

		Entity timeoutEntry = new Entity(Constants.activityKindIdentifier,
				activityStoreKey);

		String now = format.format(new Date());
		long timeout = Long.parseLong(timeoutParameter);

		timeoutEntry.setProperty("type", "timeout");
		timeoutEntry.setProperty("time", now);
		timeoutEntry.setProperty("startTime", startTime);
		timeoutEntry.setProperty("timeout", timeout);

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		datastore.put(timeoutEntry);

		System.out.println("Timeout! " + userId + "|" + startTime + "|"
				+ timeout);
	}
}