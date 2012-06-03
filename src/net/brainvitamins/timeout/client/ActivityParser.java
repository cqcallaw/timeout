package net.brainvitamins.timeout.client;

import net.brainvitamins.timeout.shared.Activity;
import net.brainvitamins.timeout.shared.Cancellation;
import net.brainvitamins.timeout.shared.Checkin;
import net.brainvitamins.timeout.shared.Timeout;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

// These exist as behaviors on the client side instead of type constructors
// because GWT uses a different (incompatible) DateFormat class on the client
// side

// JSON is not used because the author is not aware of a simple way to map
// abstract JSON representations to concrete Java types, (e.g. given a JSON
// representation of an Activity, determine that it represents a Checkin)

public class ActivityParser implements Parser<Activity>
{
	public static DateTimeFormat dateFormat = DateTimeFormat
			.getFormat("dd MMM yyyy HH:mm:ss zzz");

	// there's no reason for more than one instance to exist
	public static final ActivityParser Instance = new ActivityParser();

	protected ActivityParser()
	{
	}

	@Override
	public Activity parse(String asString)
	{
		if (asString.startsWith("Checkin")) return parseCheckin(asString);
		if (asString.startsWith("Cancellation"))
			return parseCancellation(asString);
		if (asString.startsWith("Timeout"))
			return parseTimeout(asString);
		else
			return null;
	}

	// rargh, these should live on the types that generate the string
	// representations, not here
	public static final String checkinRegExp = "Checkin \\[timestamp=(.*), timeout=(.*)\\]";
	public static final String timeoutRegExp = "Timeout \\[timestamp=(.*), timeout=(.*), startTime=(.*)\\]";
	public static final String cancellationRegExp = "Cancellation \\[timestamp=(.*)\\]";

	private static Checkin parseCheckin(String asString)
	{
		MatchResult result = RegExp.compile(checkinRegExp).exec(asString);
		if (result == null) return null;

		return new Checkin(dateFormat.parse(result.getGroup(1)),
				Long.parseLong(result.getGroup(2)));
	}

	private static Cancellation parseCancellation(String asString)
	{
		MatchResult result = RegExp.compile(cancellationRegExp).exec(asString);
		if (result == null) return null;
		return new Cancellation(dateFormat.parse(result.getGroup(1)));
	}

	private static Timeout parseTimeout(String asString)
	{
		MatchResult result = RegExp.compile(timeoutRegExp).exec(asString);
		if (result == null) return null;

		return new Timeout(dateFormat.parse(result.getGroup(1)),
				Long.parseLong(result.getGroup(2)), dateFormat.parse(result
						.getGroup(3)));
	}
}
