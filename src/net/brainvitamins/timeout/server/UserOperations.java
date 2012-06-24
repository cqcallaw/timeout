package net.brainvitamins.timeout.server;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.google.appengine.api.users.UserServiceFactory;

public class UserOperations
{
	/**
	 * Returns a detached copy of the current User data object. The activity log
	 * and recipient list are not included.
	 */
	public static User getCurrentUser()
	{
		return getUser(getCurrentUserHashedId());
	}

	public static User getUser(String userId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			User currentUser = pm.getObjectById(User.class, userId);
			User detached = pm.detachCopy(currentUser);
			return detached;
		}
		catch (JDOObjectNotFoundException e)
		{
			return null;
		}
		finally
		{
			pm.close();
		}
	}

	/**
	 * Returns a detached copy of the current user data, with the activity log.
	 */
	public static User getCurrentUserWithActivity()
	{
		return getUserWithActivity(UserOperations.getCurrentUserHashedId());
	}

	public static User getUserWithActivity(String userId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withActivityLog");
		try
		{
			User currentUser = pm.getObjectById(User.class, userId);

			User detached = pm.detachCopy(currentUser);
			return detached;
		}
		catch (JDOObjectNotFoundException e)
		{
			return null;
		}
		finally
		{
			pm.close();
		}
	}

	/**
	 * Returns a detached copy of the current user data, with the activity log.
	 */
	public static User getCurrentUserWithRecipients()
	{
		return getUserWithRecipients(getCurrentUserHashedId());
	}

	public static User getUserWithRecipients(String userId)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.getFetchPlan().addGroup("withRecipients");
		try
		{
			User currentUser = pm.getObjectById(User.class, userId);

			User detached = pm.detachCopy(currentUser);
			return detached;
		}
		catch (JDOObjectNotFoundException e)
		{
			return null;
		}
		finally
		{
			pm.close();
		}
	}

	/**
	 * @see http://stackoverflow.com/a/10604659/577298
	 * @return
	 */
	public static String getCurrentUserHashedId()
	{
		com.google.appengine.api.users.User currentGWTUser = getCurrentGWTUser();

		if (currentGWTUser == null)
			throw new IllegalStateException("Unable to obtain current user.");

		return hashUserId(currentGWTUser.getUserId());
	}

	public static com.google.appengine.api.users.User getCurrentGWTUser()
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
		// System.out.println("Hashing userId: " + userId);
		// String result = BCrypt.hashpw(userId, BCrypt.gensalt(4));
		// System.out.println("userId hashed: " + result);
		//
		// return result;
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
