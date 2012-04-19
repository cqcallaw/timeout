package net.brainvitamins.timeout.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CheckinView extends Composite
{

	private static CheckinUiBinder uiBinder = GWT.create(CheckinUiBinder.class);

	interface CheckinUiBinder extends UiBinder<Widget, CheckinView> { }

	@UiField
	FormElement checkinForm;

	@UiField
	InputElement timeoutField;

	public void setTimeout(long value)
	{
		timeoutField.setValue(String.valueOf(value));
	}

	// TODO: input validation on formAction argument
	public CheckinView(String formAction, String method)
	{
		initWidget(uiBinder.createAndBindUi(this));

		checkinForm.setAction(formAction);
		checkinForm.setMethod(method);
	}
}
