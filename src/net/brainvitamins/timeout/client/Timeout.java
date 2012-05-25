package net.brainvitamins.timeout.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.brainvitamins.timeout.client.views.MainView;
import net.brainvitamins.timeout.shared.services.ActivityService;
import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.LoginInfo;
import net.brainvitamins.timeout.shared.Recipient;
import net.brainvitamins.timeout.shared.services.ActivityServiceAsync;
import net.brainvitamins.timeout.shared.services.LoginService;
import net.brainvitamins.timeout.shared.services.LoginServiceAsync;
import net.brainvitamins.timeout.shared.services.RecipientService;
import net.brainvitamins.timeout.shared.services.RecipientServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Timeout implements EntryPoint
{
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	@SuppressWarnings("unused")
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private static final int REFRESH_INTERVAL = 500; // ms

	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the application.");
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");

	private ActivityServiceAsync activityService = GWT
			.create(ActivityService.class);

	private RecipientServiceAsync recipientService = GWT
			.create(RecipientService.class);

	private static Logger logger = Logger.getLogger("Main");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		logger.log(Level.INFO, "Started module loading.");
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login("/index.html", new AsyncCallback<LoginInfo>()
		{
			public void onFailure(Throwable error)
			{
				logger.log(Level.SEVERE, "Calling login service failed.");
			}

			public void onSuccess(LoginInfo result)
			{
				loginInfo = result;
				if (loginInfo.isLoggedIn())
				{
					logger.log(Level.INFO, "Login succeeded.");
					loadMain(loginInfo);
				}
				else
				{
					logger.log(Level.INFO, "Login required.");
					loadLogin();
				}
			}
		});
	}

	private void loadLogin()
	{
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get("content").add(loginPanel);
	}

	private void loadMain(LoginInfo loginInfo)
	{
		logger.log(Level.INFO, "Loading app");
		signOutLink.setHref(loginInfo.getLogoutUrl());
		RootPanel.get("user").add(signOutLink);

		DefaultDateTimeFormatInfo dateFormatInfo = new DefaultDateTimeFormatInfo();
		final String dateFormat = dateFormatInfo.dateTimeShort(
				dateFormatInfo.timeFormatMedium(),
				dateFormatInfo.dateFormatShort());

		final ListDataProvider<Activity> activityDataProvider = new ListDataProvider<Activity>();
		final ListDataProvider<Recipient> recipientDataProvider = new ListDataProvider<Recipient>();

		// final MainView homeView = new MainView(dateFormat,
		// activityDataProvider, recipientDataProvider);

		// ugh, so wrong and backwards to initialize a view without having data
		// attached...
		// visual layout editing fails otherwise, though
		// need a wrapper class of some kind
		final MainView homeView = new MainView(dateFormat);
		activityDataProvider.addDataDisplay(homeView.getActivityView()
				.getCellView());
		recipientDataProvider.addDataDisplay(homeView.getRecipientView()
				.getCellView());

		RootPanel.get("content").add(homeView);

		refreshActivity(activityDataProvider);

		refreshRecipients(recipientDataProvider);

		logger.log(Level.INFO, "App loaded.");

		Timer activityRefreshTimer = new Timer()
		{
			@Override
			public void run()
			{
				refreshActivity(activityDataProvider);
			}
		};

		// TODO: jitter the intervals
		// TODO: use Channels https://developers.google.com/appengine/docs/java/channel/ 
		activityRefreshTimer.scheduleRepeating(REFRESH_INTERVAL);

		Timer recipientRefreshTimer = new Timer()
		{
			@Override
			public void run()
			{
				// TODO: locking so the list doesn't get hammered by multiple
				// updates
				refreshRecipients(recipientDataProvider);
			}
		};

		recipientRefreshTimer.scheduleRepeating(REFRESH_INTERVAL);
	}

	private void refreshRecipients(
			final ListDataProvider<Recipient> dataProvider)
	{
		recipientService.getRecipients(new AsyncCallback<List<Recipient>>()
		{

			@Override
			public void onSuccess(List<Recipient> result)
			{
				List<Recipient> list = dataProvider.getList();
				list.clear();
				for (Recipient activity : result)
				{
					list.add(activity);
				}
			}

			@Override
			public void onFailure(Throwable caught)
			{
				// TODO:
			}
		});
	}

	/**
	 * 
	 */
	private void refreshActivity(final ListDataProvider<Activity> dataProvider)
	{
		activityService.getActivityLog(5, new AsyncCallback<List<Activity>>()
		{

			@Override
			public void onFailure(Throwable caught)
			{
				// TODO:
			}

			@Override
			public void onSuccess(List<Activity> result)
			{
				List<Activity> activityList = dataProvider.getList();
				activityList.clear();
				for (Activity activity : result)
				{
					activityList.add(activity);
				}
				// //TODO: awesome data model stuff so we don't have to do hax
				// like this
				//
				// long timeout = 10000;
				// // set default timeout to most recent checkin's setting
				// for (Activity entry : result)
				// {
				// if (entry.getClass().equals(Checkin.class))
				// {
				// timeout = ((Checkin) entry).getTimeout();
				// break;
				// }
				// }
				//
				// checkinForm.setTimeout(timeout);
			}
		});
	}
}