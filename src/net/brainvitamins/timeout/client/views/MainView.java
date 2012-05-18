package net.brainvitamins.timeout.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MainView extends Composite
{

	private static HomeUiBinder uiBinder = GWT.create(HomeUiBinder.class);

	interface HomeUiBinder extends UiBinder<Widget, MainView>
	{
	}

	@UiField
	ActivityView activityView;

	/**
	 * @return the activityView
	 */
	public ActivityView getActivityView()
	{
		return activityView;
	}

	@UiField
	RecipientListView recipientView;

	/**
	 * @return the recipientView
	 */
	public RecipientListView getRecipientView()
	{
		return recipientView;
	}

	private String dateFormat;

	public String getDateFormat()
	{
		return dateFormat;
	}

	public MainView(final String dateFormat)
	{
		this.dateFormat = dateFormat;

		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiFactory
	protected ActivityView getActivity()
	{
		return new ActivityView(dateFormat);
	}
}
