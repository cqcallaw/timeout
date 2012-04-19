package net.brainvitamins.timeout.client;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Checkin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class ActivityView extends Composite
{

	private static ActivityUiBinder uiBinder = GWT
			.create(ActivityUiBinder.class);

	interface ActivityUiBinder extends UiBinder<Widget, ActivityView>
	{
	}

	@UiField(provided = true)
	final CellTable<Activity> activityView = new CellTable<Activity>();

	public CellTable<Activity> getActivityView()
	{
		return activityView;
	}

	private String dateFormat;

	public String getDateFormat()
	{
		return dateFormat;
	}

	/*
	 * Construct an activity view and register it with the provided data provider, if registration hasn't occurred already
	 */
	public ActivityView(final String dateFormat,
			ListDataProvider<Activity> dataProvider)
	{
		initWidget(uiBinder.createAndBindUi(this));
		this.dateFormat = dateFormat;

		// register with the supplied data provider if we haven't already
		if (!dataProvider.getDataDisplays().contains(activityView))
			dataProvider.addDataDisplay(activityView);

		activityView.setEmptyTableWidget(new Label("No recent activity"));

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
	}
}
