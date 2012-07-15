package net.brainvitamins.timeout.client.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Checkin;
import net.brainvitamins.timeout.shared.services.ActivityService;
import net.brainvitamins.timeout.shared.services.ActivityServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ActivityView extends Composite implements CellTableView<Activity>
{
	private ActivityServiceAsync activityService = GWT
			.create(ActivityService.class);

	private static ActivityUiBinder uiBinder = GWT
			.create(ActivityUiBinder.class);

	interface ActivityUiBinder extends UiBinder<Widget, ActivityView>
	{
	}

	private static Logger logger = Logger.getLogger("ActivityView");

	@UiField(provided = true)
	final CellTable<Activity> activityView;

	public CellTable<Activity> getCellView()
	{
		return activityView;
	}

	private String dateFormat = "yyyy-MM-dd hh:MM:ss";

	public String getDateFormat()
	{
		return dateFormat;
	}

	@UiField
	TextBox timeoutField;

	@UiField
	Button checkinButton;

	public Button getCheckinButton()
	{
		return checkinButton;
	}

	public TextBox getTimeoutField()
	{
		return timeoutField;
	}
	
	public void setTimeout(long value)
	{
		timeoutField.setValue(String.valueOf(value));
	}

	public ActivityView(final String dateFormat)
	{
		if (dateFormat == null || dateFormat.isEmpty())
			throw new IllegalArgumentException(
					"dateFormat cannot be empty or null.");

		this.dateFormat = dateFormat;

		activityView = new CellTable<Activity>();
		activityView.setEmptyTableWidget(new Label("No recent activity"));

		activityView
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		TextColumn<Activity> timestampColumn = new TextColumn<Activity>()
		{
			@Override
			public String getValue(Activity activity)
			{
				return DateTimeFormat.getFormat(dateFormat).format(
						activity.getTimestamp());
			}
		};

		TextColumn<Activity> typeColumn = new TextColumn<Activity>()
		{
			@Override
			public String getValue(Activity activity)
			{
				// using Class.getSimpleName() messes with UiBinder stuff
				String className = activity.getClass().getName();
				return className.substring(className.lastIndexOf('.') + 1);
			}
		};

		TextColumn<Activity> timeoutColumn = new TextColumn<Activity>()
		{
			@Override
			public String getValue(Activity activity)
			{
				if (activity.getClass().equals(Checkin.class))
				{
					return String.valueOf(((Checkin) activity).getTimeout());
				}
				else
					return "";
			}
		};

		activityView.addColumn(timestampColumn);
		activityView.addColumn(typeColumn);
		activityView.addColumn(timeoutColumn);

		activityView.setWidth("100%", true);
		activityView.setColumnWidth(timestampColumn, 12, Unit.EM);
		activityView.setColumnWidth(typeColumn, 10, Unit.EM);
		activityView.setColumnWidth(timeoutColumn, 100, Unit.PCT);

		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("checkinButton")
	void handleCheckinClick(ClickEvent e)
	{
		// TODO: push updates to the client faster, for a tighter UI feedback
		// loop
		long timeout = Long.parseLong(timeoutField.getValue());
		activityService.checkin(timeout, new AsyncCallback<Void>()
		{

			@Override
			public void onSuccess(Void result)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(Throwable caught)
			{
				// TODO Auto-generated method stub
				// TODO: notify of checkin failure
			}
		});
	}

	@UiHandler("cancelCheckinButton")
	void handleCancelCheckinClick(ClickEvent e)
	{
		activityService.cancelCheckin(new AsyncCallback<Void>()
		{

			@Override
			public void onSuccess(Void result)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(Throwable caught)
			{
				logger.log(Level.INFO, caught.getMessage());
			}
		});
	}
}