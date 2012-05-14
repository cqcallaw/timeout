package net.brainvitamins.timeout.client.editors;

import net.brainvitamins.timeout.client.EmailRecipientProxy;
import net.brainvitamins.timeout.shared.EmailRecipient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class EmailRecipientDialog extends DialogBox
{
	interface EmailRecipientViewUiBinder extends
			UiBinder<Widget, EmailRecipientDialog>
	{
	}

	private static EmailRecipientViewUiBinder uiBinder = GWT
			.create(EmailRecipientViewUiBinder.class);

	interface Driver extends
			SimpleBeanEditorDriver<EmailRecipientProxy, EmailRecipientEditor>
	{
	}

	private Driver driver;

	// /**
	// * @return the driver
	// */
	// public Driver getDriver()
	// {
	// return driver;
	// }
	//
	private EmailRecipient editResult;

	/**
	 * @return the edit result (will be identical to the source object if no
	 *         changes have been made)
	 */
	public EmailRecipient getEditResult()
	{
		return editResult;
	}

	@UiField(provided = true)
	EmailRecipientEditor editor;

	public EmailRecipientDialog()
	{
		editor = new EmailRecipientEditor();
		driver = GWT.create(Driver.class);
		driver.initialize(editor);

		setWidget(uiBinder.createAndBindUi(this));
	}

	public void edit(EmailRecipient recipient)
	{
		driver.edit(new EmailRecipientProxy(recipient));
		this.center();
	}

	@UiHandler("okButton")
	void save(ClickEvent event)
	{
		editResult = driver.flush().getResult();

		// TODO: handle errors

		this.hide();
	}
}
