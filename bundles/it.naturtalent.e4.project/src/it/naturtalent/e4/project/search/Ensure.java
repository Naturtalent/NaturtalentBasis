package it.naturtalent.e4.project.search;

/**
 * Provides convenient methods for checking contract parameters.
 * 
 * @author Markus Gebhard
 */
public class Ensure
{
	private Ensure()
	{
		// no instance available
	}

	public static void ensureArgumentNotNull(String message, Object object)
			throws IllegalArgumentException
	{
		ensureArgumentTrue(message, object != null);
	}

	public static void ensureArgumentNotNull(Object object)
			throws IllegalArgumentException
	{
		ensureArgumentNotNull("Object must not be null", object); //$NON-NLS-1$
	}

	// public static void ensureArgumentFalse(boolean state) throws
	// IllegalArgumentException {
	// ensureArgumentTrue("boolean must be false", !state);
	// }
	//
	// public static void ensureArgumentFalse(String message, boolean state)
	// throws IllegalArgumentException {
	// ensureArgumentTrue(message, !state);
	// }
	//
	// public static void ensureArgumentTrue(boolean state) throws
	// IllegalArgumentException {
	// ensureArgumentTrue("boolean must be true", state);
	// }
	//
	public static void ensureArgumentTrue(String message, boolean state)
			throws IllegalArgumentException
	{
		if (!state)
		{
			throw new IllegalArgumentException(message);
		}
	}
}