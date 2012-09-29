package net.brainvitamins.vigilance.client.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.brainvitamins.vigilance.shared.Activity;
import net.brainvitamins.vigilance.shared.Checkin;
import net.brainvitamins.vigilance.shared.services.ActivityService;
import net.brainvitamins.vigilance.shared.services.ActivityServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ActivityView extends Composite implements CellTableView<Activity>
{
	private static Image loadingImage = new Image("loading.gif");

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

	@UiField
	Button cancelCheckinButton;

	@UiField
	Label errorLabel;

	public Button getCheckinButton()
	{
		return checkinButton;
	}

	public TextBox getTimeoutField()
	{
		return timeoutField;
	}

	/**
	 * Sets the timeout (in user units) of the
	 * 
	 * @param value
	 *            the timeout value in milliseconds
	 */
	public void setTimeout(long value)
	{
		timeoutField.setValue(String.valueOf(toUserUnits(value)));
	}

	public ActivityView(final String dateFormat)
	{
		if (dateFormat == null || dateFormat.isEmpty())
			throw new IllegalArgumentException(
					"dateFormat cannot be empty or null.");

		this.dateFormat = dateFormat;

		final NumberFormat userTimeoutFormat = NumberFormat
				.getFormat("###.###");

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
					// return String.valueOf(toUserUnits(((Checkin) activity)
					// .getTimeout()));
					return userTimeoutFormat
							.format((toUserUnits(((Checkin) activity)
									.getTimeout())));
				} else
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

		// TODO: communicate parse errors to the user
		// TODO: get user confirmation for checkin if recipients list is empty
		long timeout = fromUserUnits(Double
				.parseDouble(timeoutField.getValue()));

		lockInput(checkinButton);

		activityService.checkin(timeout, new AsyncCallback<Void>()
		{

			@Override
			public void onSuccess(Void result)
			{
				unlockInput();
				errorLabel.setText("");
				errorLabel.setVisible(false);
			}

			@Override
			public void onFailure(Throwable caught)
			{
				unlockInput();
				showError("Error checking in: " + caught
						+ " Please report this error to the administrator.");
			}
		});
	}

	@UiHandler("cancelCheckinButton")
	void handleCancelCheckinClick(ClickEvent e)
	{
		lockInput(cancelCheckinButton);

		activityService.cancelCheckin(new AsyncCallback<Void>()
		{

			@Override
			public void onSuccess(Void result)
			{
				unlockInput();
				errorLabel.setText("");
				errorLabel.setVisible(false);
			}

			@Override
			public void onFailure(Throwable caught)
			{
				unlockInput();
				showError("Error checking in: " + caught
						+ " Please report this error to the administrator.");
			}
		});
	}

	@UiHandler("timeoutField")
	public void onNameKeyKeyUp(KeyUpEvent event)
	{
		try
		{
			if(lockButton == null) //don't do anything if we're waiting on a server response 
			{
				Double.parseDouble(timeoutField.getValue());
				
				checkinButton.setEnabled(true);
				errorLabel.setText("");
				errorLabel.setVisible(false);
			}				
		} catch (NumberFormatException e)
		{
			checkinButton.setEnabled(false);
			showError("Invalid timeout.");
		}
	}

	private Button lockButton;
	private String lockButtonText;

	private void lockInput(Button source)
	{
		logger.log(Level.FINE, "Locking input...");
		if (lockButton != null)
			throw new IllegalStateException(
					"Attempted to lock an activity view that is already locked.");

		lockButton = source;
		lockButtonText = source.getText();

		source.setEnabled(false);
		timeoutField.setEnabled(false);

		source.setText("");
		source.getElement().appendChild(loadingImage.getElement());
		logger.log(Level.FINE, "Input locked.");
	}

	private void unlockInput()
	{
		logger.log(Level.FINE, "Unlocking input...");
		if (lockButton == null)
			throw new IllegalStateException(
					"Attempted to unlock an activity view that is not locked.");

		lockButton.setEnabled(true);
		timeoutField.setEnabled(true);

		lockButton.setText(lockButtonText);

		lockButton = null;
		lockButtonText = "";
		logger.log(Level.FINE, "Input unlocked.");
	}

	private void showError(String error)
	{
		errorLabel.setText(error);
		errorLabel.setVisible(true);
	}

	// TODO: proper unit conversion
	private double toUserUnits(long value)
	{
		return (double) value / 3600000.00; // to hours
	}

	private long fromUserUnits(double value)
	{
		return (long) (value * 3600000); // from hours
	}
}