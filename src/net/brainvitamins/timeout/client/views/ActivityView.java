package net.brainvitamins.timeout.client.views;

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
	final CellTable<Activity> activityView;

	public CellTable<Activity> getActivityView()
	{
		return activityView;
	}

	private String dateFormat = "yyyy-MM-dd hh:MM:ss";

	public String getDateFormat()
	{
		return dateFormat;
	}

	public ActivityView(final String dateFormat,
			ListDataProvider<Activity> dataProvider)
	{
		if (dateFormat == null || dateFormat.isEmpty())
			throw new IllegalArgumentException(
					"dateFormat cannot be empty or null.");

		if (dataProvider == null)
			throw new IllegalArgumentException("dataProvider cannot be null.");

		this.dateFormat = dateFormat;

		activityView = new CellTable<Activity>();
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

		dataProvider.addDataDisplay(activityView);

		initWidget(uiBinder.createAndBindUi(this));
	}
}
