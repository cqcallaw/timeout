package net.brainvitamins.vigilance.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.brainvitamins.vigilance.client.parsers.ActivityParser;
import net.brainvitamins.vigilance.client.parsers.RecipientParser;
import net.brainvitamins.vigilance.client.views.MainView;
import net.brainvitamins.vigilance.shared.Activity;
import net.brainvitamins.vigilance.shared.Checkin;
import net.brainvitamins.vigilance.shared.LoginInfo;
import net.brainvitamins.vigilance.shared.Recipient;
import net.brainvitamins.vigilance.shared.operations.DataOperation;
import net.brainvitamins.vigilance.shared.services.ActivityService;
import net.brainvitamins.vigilance.shared.services.ActivityServiceAsync;
import net.brainvitamins.vigilance.shared.services.ChannelService;
import net.brainvitamins.vigilance.shared.services.ChannelServiceAsync;
import net.brainvitamins.vigilance.shared.services.LoginService;
import net.brainvitamins.vigilance.shared.services.LoginServiceAsync;
import net.brainvitamins.vigilance.shared.services.RecipientService;
import net.brainvitamins.vigilance.shared.services.RecipientServiceAsync;

import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

public class Main implements EntryPoint
{
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	@SuppressWarnings("unused")
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

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

	private ChannelServiceAsync channelService = GWT
			.create(ChannelService.class);

	private static Logger logger = Logger.getLogger("Main");

	private static final ListDataProvider<Activity> activityDataProvider = new ListDataProvider<Activity>();
	private static final ListDataProvider<Recipient> recipientDataProvider = new ListDataProvider<Recipient>();

	private static final DataOperationHandler<Activity> activityDataOperationHandler = new DataOperationHandler<Activity>()
	{
		@Override
		public void add(Activity value)
		{
			List<Activity> data = activityDataProvider.getList();

			List<Activity> newData = new ArrayList<Activity>();
			newData.add(value);
			data.addAll(0, newData);

			if (data.size() > 5) data.remove(data.size() - 1);
		}

		@Override
		public void update(Activity value)
		{
			throw new IllegalStateException(
					"Activity should never been updated");
		}

		@Override
		public void delete(Activity value)
		{
			throw new IllegalStateException(
					"Activity should never been deleted");
		}
	};

	private static final DataOperationHandler<Recipient> recipientDataOperationHandler = new DataOperationHandler<Recipient>()
	{
		@Override
		public void add(Recipient value)
		{
			List<Recipient> data = recipientDataProvider.getList();
			// TODO: sorting
			data.add(value);
		}

		@Override
		public void update(Recipient value)
		{
			List<Recipient> data = recipientDataProvider.getList();

			for (int i = 0; i < data.size(); i++)
			{
				if (data.get(i).getKey().equals(value.getKey()))
				{
					data.remove(i);
					data.add(i, value);
				}
			}
		}

		@Override
		public void delete(Recipient value)
		{
			List<Recipient> data = recipientDataProvider.getList();

			for (int i = 0; i < data.size(); i++)
			{
				if (data.get(i).getKey().equals(value.getKey()))
					data.remove(i);
			}
		}
	};

	public void onModuleLoad()
	{
		logger.log(Level.INFO, "Started module loading.");
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login("/index.html", new AsyncCallback<LoginInfo>()
		{
			public void onFailure(Throwable error)
			{
				logger.log(Level.SEVERE, "Calling login service failed:"
						+ error.getMessage());
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

	DefaultDateTimeFormatInfo dateFormatInfo = new DefaultDateTimeFormatInfo();
	final String dateFormat = dateFormatInfo
			.dateTimeShort(dateFormatInfo.timeFormatMedium(),
					dateFormatInfo.dateFormatShort());

	private final MainView homeView = new MainView(dateFormat);

	private void loadMain(LoginInfo loginInfo)
	{
		logger.log(Level.INFO, "Loading app");
		signOutLink.setHref(loginInfo.getLogoutUrl());
		RootPanel.get("user").add(signOutLink);

		// ugh, so wrong and backwards to initialize a view without having a
		// data provider attached...
		// visual layout editing fails otherwise, though
		// final MainPresentation homeView = new MainPresentation(dateFormat,
		// activityDataProvider, recipientDataProvider);

		activityDataProvider.addDataDisplay(homeView.getActivityView()
				.getCellView());
		recipientDataProvider.addDataDisplay(homeView.getRecipientView()
				.getCellView());

		RootPanel.get("content").add(homeView);

		getActivity(activityDataProvider);
		getRecipients(recipientDataProvider);

		channelService.getChannelToken(new AsyncCallback<String>()
		{
			@Override
			public void onSuccess(String result)
			{
				logger.log(Level.INFO, "Got channel id: " + result);

				ChannelFactory.createChannel(result,
						new ChannelCreatedCallback()
						{
							@Override
							public void onChannelCreated(Channel channel)
							{
								channel.open(new SocketListener()
								{
									@Override
									public void onOpen()
									{
										logger.log(Level.INFO,
												"Channel opened.");
									}

									@Override
									public void onMessage(String message)
									{
										logger.log(Level.INFO,
												"Received message: " + message);

										// blargh, horrible way to do type
										// resolution...
										if (message.contains("Recipient"))
										{
											DataOperation<Recipient> dataOperation = RecipientParser.Instance
													.parse(message);
											recipientDataOperationHandler
													.handleOperation(dataOperation);
											logger.log(Level.INFO, "Parsed: "
													+ dataOperation.toString());
										}
										else
										{
											DataOperation<Activity> dataOperation = ActivityParser.Instance
													.parse(message);
											activityDataOperationHandler
													.handleOperation(dataOperation);
											logger.log(Level.INFO, "Parsed: "
													+ dataOperation.toString());
										}
									}

									@Override
									public void onError(SocketError error)
									{
										// TODO: better communication with user
										logger.log(
												Level.INFO,
												"Channel error: "
														+ error.getDescription());
									}

									@Override
									public void onClose()
									{
										logger.log(Level.INFO,
												"Channel closed.");
									}
								});
							}
						});
			}

			@Override
			public void onFailure(Throwable caught)
			{
				Window.alert("Error setting up channel: " + caught.getMessage()
						+ " Please report this message to the administrator.");
			}
		});

		// homeView.getActivityView().getTimeoutField().selectAll();
		homeView.getActivityView().getCheckinButton().setFocus(true);

		logger.log(Level.INFO, "App loaded.");
	}

	private void getRecipients(final ListDataProvider<Recipient> dataProvider)
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
				// TODO: handle getRecipients failure
			}
		});
	}

	private void getActivity(
			final ListDataProvider<Activity> activityDataProvider)
	{
		activityService.getActivityLog(5, new AsyncCallback<List<Activity>>()
		{

			@Override
			public void onFailure(Throwable caught)
			{
				// TODO: handle getActivity() failure
			}

			@Override
			public void onSuccess(List<Activity> result)
			{
				List<Activity> activityList = activityDataProvider.getList();
				activityList.clear();
				for (Activity activity : result)
				{
					activityList.add(activity);
				}

				long timeout = 3600000; //one hour
				// set default timeout to most recent checkin's setting
				for (Activity entry : result)
				{
					if (entry.getClass().equals(Checkin.class))
					{
						timeout = ((Checkin) entry).getTimeout();
						break;
					}
				}

				homeView.getActivityView().setTimeout(timeout);
			}
		});
	}
}