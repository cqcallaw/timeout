package net.brainvitamins.vigilance.server;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import net.brainvitamins.vigilance.shared.EmailRecipient;
import net.brainvitamins.vigilance.shared.Recipient;
import net.brainvitamins.vigilance.shared.operations.CreateOperation;
import net.brainvitamins.vigilance.shared.operations.DeleteOperation;
import net.brainvitamins.vigilance.shared.operations.UpdateOperation;
import net.brainvitamins.vigilance.shared.services.RecipientService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RecipientServiceImpl extends RemoteServiceServlet implements
		RecipientService {
	private static final long serialVersionUID = 6581374344337957491L;

	private static final Logger logger = Logger
			.getLogger(RecipientServiceImpl.class.getName());

	@Override
	public void addRecipient(@NotNull Recipient recipient)
			throws IllegalArgumentException {
		validateRecipient(recipient);

		addRecipientCore(recipient, null);
	}

	@Override
	public void addRecipient(@NotNull EmailRecipient recipient)
			throws IllegalArgumentException, UnsupportedEncodingException {
		validateRecipient(recipient);

		// TODO: handle email failure modes:
		// -delivery fails
		// -delivery fails remotely (initial send is a success)
		// -delivery succeeds but adding the recipient to the app engine
		// database fails

		User currentUser = UserOperations.getCurrentUser();

		if (currentUser == null) {
			throw new IllegalStateException("Unable to obtain current user.");
		}

		addRecipientCore(recipient, currentUser.getId());

		HttpServletRequest request = this.getThreadLocalRequest();

		try {
			ConfirmationRequestOperations.sendConfirmationRequest(recipient,
					currentUser, new URL("http://" + request.getServerName()
							+ ":" + request.getServerPort()
							+ "/vigilance/confirmation/email"));
		} catch (MalformedURLException e) {
			// TODO: re-throw as an exception that GWT can handle (this error
			// should be reported to the client).
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * @param recipient
	 * @param userId
	 */
	private void addRecipientCore(@NotNull Recipient recipient, String userId) {
		RecipientOperations.addRecipient(recipient, userId);
		PushOperations.pushToListener(getThreadLocalRequest().getSession()
				.getId(), new CreateOperation<Recipient>(recipient));
	}

	@Override
	public List<Recipient> getRecipients() {
		User currentUser = UserOperations.getCurrentUserWithRecipients();

		if (currentUser == null) {
			throw new IllegalStateException("Unable to obtain current user.");
		}

		List<Recipient> result = new ArrayList<Recipient>(
				currentUser.getRecipients());

		// sort by name, descending
		Collections.sort(result, new Comparator<Recipient>() {
			@Override
			public int compare(Recipient o1, Recipient o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		return result;
	}

	@Override
	public void updateRecipient(@NotNull EmailRecipient recipient)
			throws IllegalArgumentException, UnsupportedEncodingException {
		User currentUser = UserOperations.getCurrentUser();
		validateRecipient(recipient);

		HttpServletRequest request = this.getThreadLocalRequest();

		try {
			RecipientOperations.updateRecipient(
					recipient,
					currentUser,
					new URL("http://" + request.getServerName() + ":"
							+ request.getServerPort()
							+ "/vigilance/confirmation/email"));
		} catch (MalformedURLException e) {
			// TODO: re-throw as an exception that GWT can handle (this error
			// should be reported to the client).
			logger.log(Level.SEVERE, e.getMessage());
		}

		PushOperations.pushToListener(getThreadLocalRequest().getSession()
				.getId(), new UpdateOperation<Recipient>(recipient));

	}

	@Override
	public void removeRecipient(@NotNull Recipient recipient)
			throws IllegalArgumentException {
		validateRecipient(recipient);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withRecipients");

		try {
			User currentUser = pm.getObjectById(User.class,
					UserOperations.getCurrentUserId());

			if (currentUser == null) {
				throw new IllegalArgumentException("Invalid userId.");
			}

			Recipient ref = RecipientOperations.getDatabaseReference(recipient,
					currentUser.getRecipients());

			if (ref == null)
				throw new IllegalArgumentException(
						"Recipient not found in database");
			else
				currentUser.getRecipients().remove(ref);
		} finally {
			pm.close();
		}

		PushOperations.pushToListener(getThreadLocalRequest().getSession()
				.getId(), new DeleteOperation<Recipient>(recipient));
	}

	/**
	 * @param recipient
	 */
	private void validateRecipient(Recipient recipient) {
		// validation
		if (recipient == null)
			throw new IllegalArgumentException("Recipient cannot be null.");

		if (recipient.getName() == null)
			throw new IllegalArgumentException("Name cannot be null.");

		if (recipient.getName().equals(""))
			throw new IllegalArgumentException("Name cannot be empty.");
	}

	/**
	 * @param recipient
	 */
	private void validateRecipient(EmailRecipient recipient) {
		validateRecipient((Recipient) recipient);

		try {
			new InternetAddress(recipient.getAddress());
		} catch (AddressException e) {
			throw new IllegalArgumentException("Invalid email address.");
		}
	}
}
