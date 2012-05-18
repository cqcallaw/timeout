package net.brainvitamins.timeout.client.views;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Checkin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ActivityView extends Composite implements CellTableView<Activity>
{

	private static ActivityUiBinder uiBinder = GWT
			.create(ActivityUiBinder.class);

	interface ActivityUiBinder extends UiBinder<Widget, ActivityView>
	{
	}

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

	// TODO: date column sorting (client-side)
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
				if (activity.getClass().equals(Checkin.class))
				{
					return "Checkin";
				}
				else
					return "Timeout";
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
		activityView.setColumnWidth(typeColumn, 6, Unit.EM);
		activityView.setColumnWidth(timeoutColumn, 100, Unit.PCT);

		initWidget(uiBinder.createAndBindUi(this));
	}
}
