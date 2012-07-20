package net.brainvitamins.timeout.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.brainvitamins.timeout.server.PMF;
import net.brainvitamins.timeout.shared.EmailRecipient;

public class EmailRecipientConfirmationServlet extends HttpServlet
{
	private static final long serialVersionUID = -2967523178708520521L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IllegalArgumentException, IOException
	{
		String confirmationId = req.getParameter("confirmationId");

		if (confirmationId == null)
			throw new IllegalArgumentException("userId cannot be null.");

		PersistenceManager pm = PMF.get().getPersistenceManager();

		PrintWriter out = resp.getWriter();

		pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();

		EmailConfirmationRequest confirmationRequest = null;
		EmailRecipient recipient = null;
		try
		{
			tx.begin();
			confirmationRequest = pm.getObjectById(
					EmailConfirmationRequest.class, confirmationId);
			recipient = pm.getObjectById(EmailRecipient.class,
					confirmationRequest.getRecipientKey());

			if (recipient != null)
			{
				if (recipient.getAddress().equals(
						confirmationRequest.getUnverifiedAddress()))
				{
					// overwrite old value: the db key is the same;
					pm.makePersistent(recipient.withVerified(true));
					pm.deletePersistent(confirmationRequest);
					tx.commit();

					out.write("Thanks! Address confirmed.");
					// TODO: figure out a way to push the confirmation to the
					// client
				}
				else
				{
					pm.deletePersistent(confirmationRequest);
					tx.commit();

					out.write("Unverifiable request: the recipient's address has changed since the confirmation request was issued.\n"
							+ " Please contact the user directly to get a new confirmation request.");
				}
			}
			else
			{
				// recipient is no longer present in the system
				out.write("Recipient not found. Please contact the user directly to get a new confirmation request.");

			}
		}
		catch (JDOObjectNotFoundException e)
		{
			out.write("Unknown confirmation number.");
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
				System.out.println("Failed to process confirmation request "
						+ confirmationRequest + " for recipient " + recipient
						+ ". Please contact the administrator.");
			}
			pm.close();
		}
	}
}
