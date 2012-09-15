package net.brainvitamins.vigilance.client.editors;

import javax.validation.constraints.NotNull;

import net.brainvitamins.vigilance.shared.EmailRecipient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EmailRecipientEditor extends Composite
{
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 7451050279366185561L;

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Binder extends UiBinder<Widget, EmailRecipientEditor>
	{
	}

	private EmailRecipient result;

	public Boolean hasErrors()
	{
		return !(nameError.equals("") && addressError.equals(""));
	}

	@UiField
	TextBox nameEditor = new TextBox();
	private String nameError = "";

	@UiField
	Label addressErrorLabel;

	@UiField
	TextBox addressEditor = new TextBox();
	private String addressError = "";

	@UiField
	Label nameErrorLabel;

	public EmailRecipientEditor(@NotNull EmailRecipient recipient)
	{
		if (recipient == null)
			throw new IllegalArgumentException("Recipient cannot be null.");
		result = recipient;
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * Prevent further modifications until input fields are unlocked.
	 */
	public void lockInput()
	{
		nameEditor.setEnabled(false);
		addressEditor.setEnabled(false);
	}

	public void unlockInput()
	{
		nameEditor.setEnabled(true);
		addressEditor.setEnabled(true);
	}

	public EmailRecipient getCurrentValue()
	{
		return result;
	}

	@UiHandler("nameEditor")
	public void onNameKeyKeyUp(KeyUpEvent event)
	{
		try
		{
			result = result.withName(nameEditor.getText());
			nameError = "";
			nameErrorLabel.setVisible(false);
		}
		catch (IllegalArgumentException e)
		{
			nameError = e.getMessage();
			showError(nameErrorLabel, nameError);
		}
	}

	@UiHandler("addressEditor")
	public void onAddressKeyUp(KeyUpEvent event)
	{
		try
		{
			result = result.withAddress(addressEditor.getText());
			addressError = "";
			addressErrorLabel.setVisible(false);
		}
		catch (IllegalArgumentException e)
		{
			addressError = e.getMessage();
			showError(addressErrorLabel, addressError);
		}
	}

	private void showError(Label errorLabel, String error)
	{
		errorLabel.setText(error);
		errorLabel.setVisible(true);
	}
}
