package net.brainvitamins.timeout.client.parsers;

import net.brainvitamins.timeout.shared.operations.CreateOperation;
import net.brainvitamins.timeout.shared.operations.DataOperation;
import net.brainvitamins.timeout.shared.operations.DeleteOperation;
import net.brainvitamins.timeout.shared.operations.UpdateOperation;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

//These parsing behaviors are defined on the client side instead of in type constructors
//because GWT uses a different (incompatible) DateFormat class on the client
//side

//JSON is not used because the author is not aware of a simple way to map
//abstract JSON representations to concrete Java types, (e.g. given a JSON
//representation of an Activity, determine that it represents a Checkin)

public abstract class DataOperationParser<T>
{
	public static DateTimeFormat dateFormat = DateTimeFormat
			.getFormat("dd MMM yyyy HH:mm:ss zzz");

	public static final String dataOperationRegExpString = "(Create|Update|Delete)Operation \\[subject=(.*)\\]";

	public static final RegExp dataOperationRegExp = RegExp
			.compile(dataOperationRegExpString);

	public DataOperation<T> parse(String asString)
	{
		MatchResult result = dataOperationRegExp.exec(asString);
		if (result == null)
			throw new IllegalArgumentException(
					"Unrecognized data operation type.");

		String itemString = result.getGroup(2);

		if (asString.startsWith("CreateOperation"))
			return new CreateOperation<T>(parseItem(itemString));
		else if (asString.startsWith("UpdateOperation"))
			return new UpdateOperation<T>(parseItem(itemString));
		else
			// (asString.startsWith("DeleteOperation"))
			return new DeleteOperation<T>(parseItem(itemString));
	}

	public abstract T parseItem(String asString);
}
