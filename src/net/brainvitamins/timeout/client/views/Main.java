package net.brainvitamins.timeout.client.views;

import net.brainvitamins.timeout.shared.Activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class Main extends Composite
{

	private static HomeUiBinder uiBinder = GWT.create(HomeUiBinder.class);

	interface HomeUiBinder extends UiBinder<Widget, Main>
	{
	}

	private ListDataProvider<Activity> activityDataProvider;

	@UiField
	ActivityView activityView;

	@UiField
	CheckinView checkinView = new CheckinView();

	private String dateFormat;

	public String getDateFormat()
	{
		return dateFormat;
	}

	public Main(final String dateFormat,
			ListDataProvider<Activity> activityDataProvider)
	{
		this.dateFormat = dateFormat;
		this.activityDataProvider = activityDataProvider;

		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiFactory
	protected ActivityView getActivity()
	{
		if (activityDataProvider == null)
			throw new IllegalStateException(
					"Cannot initialize activity without activity data provider.");
		return new ActivityView(dateFormat, activityDataProvider);
	}
}
