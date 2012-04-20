package net.brainvitamins.timeout.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.brainvitamins.timeout.shared.Checkin;

public class CheckinServlet extends HttpServlet
{
	private static final long serialVersionUID = -9209388711200753616L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException
	{
		Date timestamp = new Date();

		long timeout = Long.parseLong(req.getParameter("timeout"));

		if (timeout < 1)
			throw new IllegalArgumentException(
					"Parameter timeout cannot be less than one (was it defined?)");

		Constants.ACTIVITYSERVICE.logActivity(new Checkin(timestamp, timeout));

		resp.sendRedirect("/");
	}
}
