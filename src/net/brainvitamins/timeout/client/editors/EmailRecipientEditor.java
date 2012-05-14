package net.brainvitamins.timeout.client.editors;

import net.brainvitamins.timeout.client.EmailRecipientProxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EmailRecipientEditor extends Composite implements
		Editor<EmailRecipientProxy>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7451050279366185561L;

	private static Binder uiBinder = GWT
			.create(Binder.class);

	interface Binder extends
			UiBinder<Widget, EmailRecipientEditor>
	{
	}

	public interface Driver extends
			SimpleBeanEditorDriver<EmailRecipientProxy, EmailRecipientEditor>
	{
	};

	@UiField
	TextBox nameEditor = new TextBox();

	@UiField
	TextBox addressEditor = new TextBox();

	public EmailRecipientEditor()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
}
