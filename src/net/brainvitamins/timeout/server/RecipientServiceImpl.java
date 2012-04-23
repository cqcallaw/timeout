package net.brainvitamins.timeout.server;

import java.util.ArrayList;
import java.util.List;

import net.brainvitamins.timeout.client.services.RecipientService;
import net.brainvitamins.timeout.shared.Recipient;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RecipientServiceImpl extends RemoteServiceServlet implements
		RecipientService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6581374344337957491L;
	
	public static final String recipientKindIdentifier = "Recipient";

	@Override
	public void addRecipient()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public List<Recipient> getRecipients()
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		String userId = user.getUserId();
		
		Key recipientStoreKey = KeyFactory.createKey(recipientKindIdentifier, userId);
		
		Query query = new Query(recipientKindIdentifier, recipientStoreKey);

		List<Entity> activityEntries = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		
		List<Recipient> recipients = new ArrayList<Recipient>(); 

		
		
		return recipients;
	}

}
