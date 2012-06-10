package net.brainvitamins.timeout.client.parsers;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class RecipientParser extends DataOperationParser<Recipient>
{
	public static final RecipientParser Instance = new RecipientParser();

	protected RecipientParser()
	{
	}

	@Override
	public Recipient parseItem(String asString)
	{
		if (asString.startsWith("EmailRecipient"))
			return parseEmailRecipient(asString);
		else
			throw new IllegalArgumentException("Unrecognized activity type.");
	}

	private EmailRecipient parseEmailRecipient(String asString)
	{
		MatchResult result = emailRecipientRegExp.exec(asString);
		if (result == null)
			throw new IllegalArgumentException(
					"Unrecognized EmailRecipient string representation.");

		return new EmailRecipient(result.getGroup(1), result.getGroup(2),
				Boolean.parseBoolean(result.getGroup(3)), result.getGroup(4));
	}

	public static final String emailRecipientRegExpString = "EmailRecipient \\[name=(.*), address=(.*), verified=(.*), dbKey=(.*)\\]";

	public static final RegExp emailRecipientRegExp = RegExp
			.compile(emailRecipientRegExpString);

}
