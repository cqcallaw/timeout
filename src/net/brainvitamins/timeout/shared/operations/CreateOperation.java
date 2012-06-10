package net.brainvitamins.timeout.shared.operations;

public class CreateOperation<T> extends DataOperation<T>
{
	public CreateOperation(T subject)
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
		return "CreateOperation [subject=" + getSubject() + "]";
	}
}
