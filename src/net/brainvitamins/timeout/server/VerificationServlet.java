package net.brainvitamins.timeout.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.brainvitamins.timeout.shared.Recipient;

public class VerificationServlet extends HttpServlet
{
	private static final long serialVersionUID = -2967523178708520521L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IllegalArgumentException, IOException
	{
		String userId = req.getParameter("userId");
		String recipientId = req.getParameter("recipientId");

		if (userId == null)
			throw new IllegalArgumentException("userId cannot be null.");

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try
		{
			User user = pm.getObjectById(User.class, userId);

			if (user == null)
				throw new IllegalArgumentException("Invalid userId.");

			boolean foundRecipient = false;
			Set<Recipient> recipients = user.getRecipients();
			for (Recipient recipient : recipients)
			{
				if (recipient.getKey().equals(recipientId))
				{
					RecipientOperations.updateRecipient(
							recipient.withVerified(true), userId);
					foundRecipient = true;
					break;
				}
			}

			PrintWriter out = resp.getWriter();

			out.write(foundRecipient ? "Thanks! Address confirmed."
					: "Address not found.");

			// TODO: figure out a way to push the confirmation to the client
		}
		finally
		{
			pm.close();
		}
	}
}
