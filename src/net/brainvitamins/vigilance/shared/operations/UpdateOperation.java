package net.brainvitamins.vigilance.shared.operations;

public class UpdateOperation<T> extends DataOperation<T>
{
	public UpdateOperation(T subject)
	{
		super(subject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "UpdateOperation [subject=" + getSubject() + "]";
	}
}
