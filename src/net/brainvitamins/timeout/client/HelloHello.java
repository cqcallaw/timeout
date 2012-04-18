package net.brainvitamins.timeout.client;

import java.util.List;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Checkin;
import net.brainvitamins.timeout.shared.LoginInfo;
import net.brainvitamins.timeout.shared.Timeout;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HelloHello implements EntryPoint
{
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the application.");
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login("/index.html", new AsyncCallback<LoginInfo>()
		{
			public void onFailure(Throwable error)
			{
			}

			public void onSuccess(LoginInfo result)
			{
				loginInfo = result;
				if (loginInfo.isLoggedIn())
				{
					loadMain(loginInfo);
				}
				else
				{
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
		RootPanel.get("user").add(loginPanel);
	}

	private void loadMain(LoginInfo loginInfo)
	{
		signOutLink.setHref(loginInfo.getLogoutUrl());
		RootPanel.get("user").add(signOutLink);

		final CheckinForm checkinForm = new CheckinForm(GWT.getModuleBaseURL()
				.concat("checkin"), FormPanel.METHOD_POST);
		RootPanel.get("checkin").add(checkinForm);

		final RootPanel activityPanel = RootPanel.get("activity");
		final FlexTable activityView = new FlexTable();

		activityPanel.add(activityView);

		ActivityServiceAsync activityService = GWT
				.create(ActivityService.class);

		activityService.getActivityLog(5, new AsyncCallback<List<Activity>>()
		{

			@Override
			public void onFailure(Throwable caught)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<Activity> result)
			{
				if (result.isEmpty())
				{
					activityPanel.add(new Label("No recent activity"));
				}
				else
				{
					Long timeout = new Long(10000);

					boolean timeoutSet = false;

					DefaultDateTimeFormatInfo dateFormatInfo = new DefaultDateTimeFormatInfo();
					String dateFormat = dateFormatInfo.dateTimeShort(
							dateFormatInfo.timeFormatShort(),
							dateFormatInfo.dateFormatShort());

					int rowIndex = 0;
					for (Activity entry : result)
					{
						if (entry.getClass().equals(Checkin.class))
						{
							Checkin logEntry = (Checkin) entry;

							long entryTimeout = logEntry.getTimeout();
							// use the timeout from the most recent checkin as
							// the
							// default for the next checkin timeout
							if (!timeoutSet)
							{
								timeout = entryTimeout;
								timeoutSet = true;
							}

							activityView.setText(rowIndex, 0,
									DateTimeFormat.getFormat(dateFormat)
											.format(logEntry.getTimestamp()));

							activityView.setText(rowIndex, 1, "Checkin");

							activityView.setText(rowIndex, 2,
									String.valueOf(entryTimeout));
						}

						else if (entry.getClass().equals(Timeout.class))
						{
							Timeout logEntry = (Timeout) entry;

							activityView.setText(rowIndex, 0,
									DateTimeFormat.getFormat(dateFormat)
											.format(logEntry.getTimestamp()));

							activityView.setText(rowIndex, 1, "Timeout");
						}

						rowIndex++;
					}

					checkinForm.setTimeout(timeout);
				}
			}
		});

	}
}