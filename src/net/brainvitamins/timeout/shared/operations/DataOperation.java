package net.brainvitamins.timeout.shared.operations;

public abstract class DataOperation<T>
{
	private T subject;

	public T getSubject()
	{
		return subject;
	}

	public DataOperation(T subject)
	{
		super();
		this.subject = subject;
	}
}
