package net.brainvitamins.timeout.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.QueueFactory;

public class Constants
{
	public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final ActivityLogger ACTIVITYSERVICE = new ActivityLogger(
			DatastoreServiceFactory.getDatastoreService(),
			QueueFactory.getDefaultQueue(), "Activity");

	public static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
}
