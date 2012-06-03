package net.brainvitamins.timeout.client;

public interface Parser<T>
{
	public T parse(String asString);
}
