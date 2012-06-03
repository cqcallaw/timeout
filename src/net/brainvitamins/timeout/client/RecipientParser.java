package net.brainvitamins.timeout.client;

import net.brainvitamins.timeout.shared.EmailRecipient;
import net.brainvitamins.timeout.shared.Recipient;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class RecipientParser implements Parser<Recipient>
{
	public static final RecipientParser Instance = new RecipientParser();

	protected RecipientParser()
	{
	}

	@Override
	public Recipient parse(String asString)
	{
		if (asString.startsWith("EmailRecipient"))
			return parseEmailRecipient(asString);
		else
			return null;
	}

	private EmailRecipient parseEmailRecipient(String asString)
	{
		MatchResult result = RegExp.compile(emailRecipientRegExp)
				.exec(asString);
		if (result == null) return null;

		return new EmailRecipient(result.getGroup(1), result.getGroup(2),
				Boolean.parseBoolean(result.getGroup(3)));
	}

	public static final String emailRecipientRegExp = "EmailRecipient \\[name=(.*), address=(.*), verified=(.*)\\]";
}
