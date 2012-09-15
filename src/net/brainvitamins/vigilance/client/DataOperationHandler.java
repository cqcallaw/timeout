package net.brainvitamins.vigilance.client;

import net.brainvitamins.vigilance.shared.operations.CreateOperation;
import net.brainvitamins.vigilance.shared.operations.DataOperation;
import net.brainvitamins.vigilance.shared.operations.DeleteOperation;
import net.brainvitamins.vigilance.shared.operations.UpdateOperation;

public abstract class DataOperationHandler<T>
{
	public void handleOperation(DataOperation<T> dataOperation) throws IllegalArgumentException
	{
		if (dataOperation instanceof CreateOperation<?>)
			add(dataOperation.getSubject());
		else if (dataOperation instanceof UpdateOperation<?>)
			update(dataOperation.getSubject());
		else if (dataOperation instanceof DeleteOperation<?>)
			delete(dataOperation.getSubject());
		else
			throw new IllegalArgumentException(
					"Unrecognized data operation type.");
	}

	public abstract void add(T value);

	public abstract void update(T value);

	public abstract void delete(T value);
}
