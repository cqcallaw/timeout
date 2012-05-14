package net.brainvitamins.timeout.client.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.brainvitamins.timeout.client.editors.EmailRecipientDialog;
import net.brainvitamins.timeout.client.services.RecipientService;
import net.brainvitamins.timeout.client.services.RecipientServiceAsync;
import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class RecipientListView extends Composite implements
		CellTableView<Recipient>
{

	private static ActivityUiBinder uiBinder = GWT
			.create(ActivityUiBinder.class);

	interface ActivityUiBinder extends UiBinder<Widget, RecipientListView>
	{
	}

	private RecipientServiceAsync recipientService = GWT
			.create(RecipientService.class);

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

	private static Logger logger = Logger.getLogger("RecipientList");

	public RecipientListView()
	{
		recipientView = new CellTable<Recipient>();

		// ref: http://stackoverflow.com/q/10454435/577298
		recipientView
				.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		recipientView.setEmptyTableWidget(new Label("No recipients."));

		// Column<Recipient, String> typeColumn = new Column<Recipient, String>(
		// new ImageCell())
		// {
		// @Override
		// public String getValue(Recipient object)
		// {
		// if (object.getClass().equals(EmailRecipient.class))
		// {
		// return "mail.png";
		// }
		// else
		// {
		// // TODO: test "unknown" scenario
		// return "unknown.png";
		// }
		// }
		// };

		Column<Recipient, Recipient> removeColumn = new Column<Recipient, Recipient>(
				new RemoveCell())
		{
			@Override
			public Recipient getValue(Recipient object)
			{
				return object;
			}
		};

		TextColumn<Recipient> nameColumn = new TextColumn<Recipient>()
		{
			@Override
			public String getValue(Recipient recipient)
			{
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

		// recipientView.addColumn(typeColumn);
		recipientView.addColumn(nameColumn);
		recipientView.addColumn(addressColumn);
		recipientView.addColumn(removeColumn);

		recipientView.setWidth("100%", true);
		// recipientView.setColumnWidth(typeColumn, 48, Unit.PX);
		recipientView.setColumnWidth(nameColumn, 10, Unit.PCT);
		recipientView.setColumnWidth(addressColumn, 80, Unit.PCT);
		recipientView.setColumnWidth(removeColumn, 48, Unit.PX);

		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("addButton")
	void handleClick(ClickEvent e)
	{
		EmailRecipient recipient = new EmailRecipient();

		final EmailRecipientDialog testView = new EmailRecipientDialog();

		testView.edit(recipient);

		testView.addCloseHandler(new CloseHandler<PopupPanel>()
		{
			@Override
			public void onClose(CloseEvent<PopupPanel> event)
			{
				EmailRecipient result = testView.getEditResult();
				if (result != null)
				{
					// TODO: field validation
					logger.log(Level.INFO, "Update required.");
					// send recipient to server
					recipientService.addRecipient(result,
							new AsyncCallback<Void>()
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

								}
							});
				}
			}
		});
	}
}
