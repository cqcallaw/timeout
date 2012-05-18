package net.brainvitamins.timeout.client.views;

import net.brainvitamins.timeout.shared.services.ActivityService;
import net.brainvitamins.timeout.shared.services.ActivityServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CheckinView extends Composite
{
	private static CheckinUiBinder uiBinder = GWT.create(CheckinUiBinder.class);

	interface CheckinUiBinder extends UiBinder<Widget, CheckinView>
	{
	}

	private ActivityServiceAsync activityService = GWT
			.create(ActivityService.class);

	@UiField
	InputElement timeoutField;

	@UiField
	Button checkinButton;

	public void setTimeout(long value)
	{
		timeoutField.setValue(String.valueOf(value));
	}

	public CheckinView()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("checkinButton")
	void handleClick(ClickEvent e)
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
}
