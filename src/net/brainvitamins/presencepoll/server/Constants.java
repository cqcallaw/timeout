package net.brainvitamins.presencepoll.server;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.QueueFactory;


public class Constants
{
	public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final ActivityService SERVICE = new ActivityService(
			DatastoreServiceFactory.getDatastoreService(),
			QueueFactory.getDefaultQueue(), "Activity");
}
