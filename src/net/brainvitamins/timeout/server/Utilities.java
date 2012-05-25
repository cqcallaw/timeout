package net.brainvitamins.timeout.server;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.appengine.api.users.UserServiceFactory;

public class Utilities
{
	/**
	 * 
	 * @see http://stackoverflow.com/a/10604659/577298
	 * @return
	 */
	public static String getCurrentUserHashedId()
	{
		return hashUserId(getGWTUser().getUserId());
	}

	public static com.google.appengine.api.users.User getGWTUser()
	{
		return UserServiceFactory.getUserService().getCurrentUser();
	}

	/**
	 * @see http://stackoverflow.com/a/415971/577298
	 * @see http://stackoverflow.com/a/416829/577298
	 * @param userId
	 * @return
	 */
	public static String hashUserId(String userId)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] bytes = md.digest((Constants.SALT + userId).getBytes(Charset
					.forName("UTF-8")));
			return new String(encodeHex(bytes));
		}
		catch (NoSuchAlgorithmException e)
		{
			// SHA *should* always be available.
			// see
			// http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#MessageDigest
			throw new AssertionError("SHA-512 unknown.");
		}
	}

	// the following code is copied from the Apache Commons Codec library,
	// to avoid introducing a new
	// dependency for a single function.
	// http://svn.apache.org/viewvc/commons/proper/codec/tags/1_6/src/main/java/org/apache/commons/codec/binary/Hex.java
	// Apache Commons Codec library is released under the Apache 2.0 license,
	// see http://www.apache.org/licenses/LICENSE-2.0.html

	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static char[] encodeHex(byte[] data)
	{
		return encodeHex(data, DIGITS_UPPER);
	}

	static char[] encodeHex(byte[] data, char[] toDigits)
	{
		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++)
		{
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}

		return out;
	}
}
