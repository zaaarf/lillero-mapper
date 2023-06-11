package ftbsc.lll.exceptions;

import ftbsc.lll.mapper.IMapper;

/**
 * Thrown when a given {@link IMapper} cannot uniquely identify a mapping with
 * the given data.
 */
public class AmbiguousMappingException extends RuntimeException {

	/**
	 * Constructs a new ambiguous mapping definition exception with the specified detail message.
	 * @param message the detail message
	 */
	public AmbiguousMappingException(String message) {
		super(message);
	}

	/**
	 * Constructs a new ambiguous definition exception with the specified detail message and cause.
	 * @param  message the detail message
	 * @param  cause the cause, may be null (indicating nonexistent or unknown cause)
	 */
	public AmbiguousMappingException(String message, Throwable cause) {
		super(message, cause);
	}
}