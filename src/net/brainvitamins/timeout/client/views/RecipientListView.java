package net.brainvitamins.timeout.client.views;

import net.brainvitamins.timeout.client.editors.EmailRecipientEditorDialog;
import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RecipientListView extends Composite implements
		CellTableView<Recipient>
{

	private static ActivityUiBinder uiBinder = GWT
			.create(ActivityUiBinder.class);

	interface ActivityUiBinder extends UiBinder<Widget, RecipientListView>
	{
	}

	@UiField(provided = true)
	final CellTable<Recipient> recipientView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.brainvitamins.timeout.client.views.CellTableView#getCellView()
	 */
	@Override
	public CellTable<Recipient> getCellView()
	{
		return recipientView;
	}

	@UiField
	Button addButton;

	public RecipientListView()
	{
		recipientView = new CellTable<Recipient>();

		// ref: http://stackoverflow.com/q/10454435/577298
		recipientView
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		recipientView.setEmptyTableWidget(new Label("No recipients."));

		TextColumn<Recipient> nameColumn = new TextColumn<Recipient>()
		{
			@Override
			public String getValue(Recipient recipient)
			{
				if (recipient instanceof EmailRecipient)
				{
					String prefix = ((EmailRecipient) recipient).isVerified() ? ""
							: "(Unverified) ";
					return prefix + recipient.getName();
				}
				else
					return recipient.getName();
			}
		};

		TextColumn<Recipient> addressColumn = new TextColumn<Recipient>()
		{
			@Override
			public String getValue(Recipient recipient)
			{
				if (recipient.getClass().equals(EmailRecipient.class))
				{
					EmailRecipient concrete = (EmailRecipient) recipient;
					return concrete.getAddress();
				}
				else
				{
					return "";
				}
			}
		};

		Column<Recipient, Recipient> editColumn = new Column<Recipient, Recipient>(
				new EditCell())
		{
			@Override
			public Recipient getValue(Recipient object)
			{
				return object;
			}
		};

		Column<Recipient, Recipient> removeColumn = new Column<Recipient, Recipient>(
				new RemoveCell())
		{
			@Override
			public Recipient getValue(Recipient object)
			{
				return object;
			}
		};

		recipientView.addColumn(nameColumn);
		recipientView.addColumn(addressColumn);
		recipientView.addColumn(editColumn);
		recipientView.addColumn(removeColumn);

		recipientView.setWidth("100%", true);
		recipientView.setColumnWidth(nameColumn, 25, Unit.PCT);
		recipientView.setColumnWidth(addressColumn, 75, Unit.PCT);
		recipientView.setColumnWidth(editColumn, 38, Unit.PX);
		recipientView.setColumnWidth(removeColumn, 38, Unit.PX);

		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("addButton")
	void handleClick(ClickEvent e)
	{
		// ideally this would be generalized to handle non-email recipients
		EmailRecipient recipient = new EmailRecipient();
		EmailRecipientEditorDialog editDialog = new EmailRecipientEditorDialog();
		editDialog.edit(recipient);
	}
}
