package net.brainvitamins.timeout.client.editors;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.services.RecipientService;
import net.brainvitamins.timeout.shared.services.RecipientServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EmailRecipientEditorDialog extends DialogBox
{
	interface EmailRecipientViewUiBinder extends
			UiBinder<Widget, EmailRecipientEditorDialog>
	{
	}

	private static EmailRecipientViewUiBinder uiBinder = GWT
			.create(EmailRecipientViewUiBinder.class);

	interface Driver extends
			SimpleBeanEditorDriver<EmailRecipientProxy, EmailRecipientEditor>
	{
	}

	private Driver driver;

	private RecipientServiceAsync recipientService = GWT
			.create(RecipientService.class);

	private static Image img = new Image("loading.gif");

	private EmailRecipient original;

	@UiField(provided = true)
	EmailRecipientEditor editor;

	@UiField
	Button okButton;

	@UiField
	Label errorLabel;

	public EmailRecipientEditorDialog()
	{
		editor = new EmailRecipientEditor();
		driver = GWT.create(Driver.class);
		driver.initialize(editor);

		KeyPressHandler errorReset = new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				errorLabel.setVisible(false);
			}
		};

		editor.nameEditor.addKeyPressHandler(errorReset);

		editor.addressEditor.addKeyPressHandler(errorReset);

		setWidget(uiBinder.createAndBindUi(this));
	}

	public void add()
	{
		driver.edit(new EmailRecipientProxy());
		this.center();
	}

	public void edit(EmailRecipient recipient)
	{
		original = recipient;
		driver.edit(new EmailRecipientProxy(recipient));
		this.center();
	}

	@UiHandler("okButton")
	void save(ClickEvent event)
	{
		errorLabel.setText("");
		lockInput();
		EmailRecipient result = driver.flush().getResult();

		if (result != null)
		{
			// TODO: client-side input validation (empty fields, invalid e-mail
			// addresses, etc)

			// the original is magically mysteriously initialized by the time we
			// reach this point. Odd.
			if (original.equals(new EmailRecipient()))
			{
				recipientService.addRecipient(result, serverHandler);
			}
			else if (!(original.equals(result)))
			{
				recipientService.updateRecipient(result, serverHandler);
			}
			else
			{
				unlockInput();
			}
		}
		else
		{
			unlockInput();
		}
	}

	@UiHandler("cancelButton")
	void cancel(ClickEvent event)
	{
		this.hide();
	}

	private final EmailRecipientEditorDialog currentDialog = this;

	private AsyncCallback<Void> serverHandler = new AsyncCallback<Void>()
	{
		@Override
		public void onSuccess(Void result)
		{
			currentDialog.hide();
		}

		@Override
		public void onFailure(Throwable caught)
		{
			if (caught instanceof IllegalArgumentException)
				errorLabel.setText(caught.getMessage());
			else
				errorLabel
						.setText("Server error while adding recipient.\nPlease contact the server admin.");

			errorLabel.setVisible(true);

			unlockInput();
		}
	};

	private void lockInput()
	{
		editor.lockInput();
		okButton.setEnabled(false);

		okButton.setText("");
		okButton.getElement().appendChild(img.getElement());
	}

	private void unlockInput()
	{
		editor.unlockInput();
		okButton.setEnabled(true);

		okButton.setText("OK");
		okButton.getElement().removeChild(img.getElement());
	}
}
