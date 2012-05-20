package net.brainvitamins.timeout.client.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.brainvitamins.timeout.client.editors.EmailRecipientEditorDialog;
import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;

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

/**
 * A custom {@link Cell} to show
 */
public class EditCell extends AbstractCell<Recipient>
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
		@SafeHtmlTemplates.Template("<img src=\"edit.png\" />")
		SafeHtml cell();
	}

	/**
	 * Create a singleton instance of the templates used to render the cell.
	 */
	private static Templates templates = GWT.create(Templates.class);

	private static Logger logger = Logger.getLogger("EditCell");

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			Recipient value, SafeHtmlBuilder sb)
	{
		if (value == null) return;
		SafeHtml rendered;
		rendered = templates.cell();
		sb.append(rendered);
	}

	public EditCell()
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
				logger.log(Level.INFO, "EditCell clicked.");

				// TODO: refactor to support other Recipient types
				if (value instanceof EmailRecipient)
				{
					EmailRecipientEditorDialog editDialog = new EmailRecipientEditorDialog();
					editDialog.edit((EmailRecipient) value);
				}
			}
		}
	}
}
