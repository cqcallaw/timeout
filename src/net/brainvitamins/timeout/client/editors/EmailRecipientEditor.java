package net.brainvitamins.timeout.client.editors;


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

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Binder extends UiBinder<Widget, EmailRecipientEditor>
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
		nameEditor.setFocus(true);
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * Lock input fields to prevent further changes (unless unlocked)
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
}
