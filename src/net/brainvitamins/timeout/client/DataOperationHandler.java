package net.brainvitamins.timeout.client;

import net.brainvitamins.timeout.shared.operations.CreateOperation;
import net.brainvitamins.timeout.shared.operations.DataOperation;
import net.brainvitamins.timeout.shared.operations.DeleteOperation;
import net.brainvitamins.timeout.shared.operations.UpdateOperation;

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
