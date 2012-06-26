package net.brainvitamins.timeout.client.parsers;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Cancellation;
import net.brainvitamins.timeout.shared.Checkin;
import net.brainvitamins.timeout.shared.Timeout;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class ActivityParser extends DataOperationParser<Activity>
{
	// there's no reason for more than one instance to exist
	public static final ActivityParser Instance = new ActivityParser();
	protected ActivityParser()
	{
	}

	@Override
	public Activity parseItem(String asString)
	{
		if (asString.startsWith("Checkin"))
			return parseCheckin(asString);
		else if (asString.startsWith("Cancellation"))
			return parseCancellation(asString);
		else if (asString.startsWith("Timeout"))
			return parseTimeout(asString);
		else
			throw new IllegalArgumentException("Unrecognized activity type.");
	}

	// rargh, these should live on the types that generate the string
	// representations, not here
	public static final String checkinRegExp = "Checkin \\[timestamp=(.*), timeout=(.*)\\]";
	public static final String timeoutRegExp = "Timeout \\[timestamp=(.*), timeout=(.*), startTime=(.*)\\]";
	public static final String cancellationRegExp = "Cancellation \\[timestamp=(.*)\\]";

	private static Checkin parseCheckin(String asString)
	{
		MatchResult result = RegExp.compile(checkinRegExp).exec(asString);
		if (result == null)
			throw new IllegalArgumentException(
					"Unrecognized Checkin string representation.");

		return new Checkin(dateFormat.parse(result.getGroup(1)),
				Long.parseLong(result.getGroup(2)));
	}

	private static Cancellation parseCancellation(String asString)
	{
		MatchResult result = RegExp.compile(cancellationRegExp).exec(asString);
		if (result == null)
			throw new IllegalArgumentException(
					"Unrecognized Cancellation string representation.");

		return new Cancellation(dateFormat.parse(result.getGroup(1)));
	}

	private static Timeout parseTimeout(String asString)
	{
		MatchResult result = RegExp.compile(timeoutRegExp).exec(asString);
		if (result == null)
			throw new IllegalArgumentException(
					"Unrecognized Timeout string representation.");

		return new Timeout(dateFormat.parse(result.getGroup(1)),
				Long.parseLong(result.getGroup(2)), dateFormat.parse(result
						.getGroup(3)));
	}
}
