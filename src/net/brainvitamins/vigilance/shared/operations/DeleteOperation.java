package net.brainvitamins.vigilance.shared.operations;

public class DeleteOperation<T> extends DataOperation<T>
{
	public DeleteOperation(T subject)
	{
		super(subject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.subject#toString()
	 */
	@Override
	public String toString()
	{
		return "DeleteOperation [subject=" + getSubject() + "]";
	}
}
