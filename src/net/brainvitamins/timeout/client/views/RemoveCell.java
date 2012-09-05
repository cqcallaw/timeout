package net.brainvitamins.timeout.client.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.brainvitamins.timeout.shared.Recipient;
import net.brainvitamins.timeout.shared.services.RecipientService;
import net.brainvitamins.timeout.shared.services.RecipientServiceAsync;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A custom {@link Cell}, designed to allow the user to remove a recipient
 */
public class RemoveCell extends AbstractCell<Recipient>
{
	/**
	 * The HTML templates used to render the cell.
	 */
	interface Templates extends SafeHtmlTemplates
	{
		/**
		 * The template for this Cell, which includes styles and a value.
		 * 
		 * @param styles
		 *            the styles to include in the style attribute of the div
		 * @param value
		 *            the safe value. Since the value type is {@link SafeHtml},
		 *            it will not be escaped before including it in the
		 *            template. Alternatively, you could make the value type
		 *            String, in which case the value would be escaped.
		 * @return a {@link SafeHtml} instance
		 */
		@SafeHtmlTemplates.Template("<img src=\"remove.png\" />")
		SafeHtml cell();
	}

	/**
	 * Create a singleton instance of the templates used to render the cell.
	 */
	private static Templates templates = GWT.create(Templates.class);

	private static Logger logger = Logger.getLogger("RemoveCell");

	private static RecipientServiceAsync recipientService = GWT
			.create(RecipientService.class);

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			Recipient value, SafeHtmlBuilder sb)
	{
		if (value == null) return;

		SafeHtml rendered;
		rendered = templates.cell();
		sb.append(rendered);
	}

	public RemoveCell()
	{
		super("click");
	}

	@Override
	public void onBrowserEvent(Context context, Element parent,
			Recipient value, NativeEvent event,
			ValueUpdater<Recipient> valueUpdater)
	{
		// Handle the click event.
		if ("click".equals(event.getType()))
		{
			// Ignore clicks that occur outside of the outermost element.
			EventTarget eventTarget = event.getEventTarget();
			if (parent.getFirstChildElement().isOrHasChild(
					Element.as(eventTarget)))
			{
				logger.log(Level.INFO, "RemoveCell clicked.");

				if (Window.confirm("Remove recipient " + value.getName() + "?"))
				{
					recipientService.removeRecipient(value,
							new AsyncCallback<Void>()
							{

								@Override
								public void onSuccess(Void result)
								{
									logger.log(Level.INFO, "Recipient removed.");
								}

								@Override
								public void onFailure(Throwable caught)
								{
									Window.alert("Error removing recipient: "
											+ caught.getMessage());
								}
							});
				}
			}
		}
	}
}
