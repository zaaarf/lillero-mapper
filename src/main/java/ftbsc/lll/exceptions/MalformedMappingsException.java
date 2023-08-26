package ftbsc.lll.exceptions;

/**
 * Thrown when something goes wrong while parsing a mappings file.
 */
public class MalformedMappingsException extends RuntimeException {

	/**
	 * Constructs a new {@link MalformedMappingsException} given the line number
	 * and an error message.
	 * @param lineNumber the line the error occurred at
	 * @param error the error message
	 */
	public MalformedMappingsException(int lineNumber, String error) {
		super(String.format("Unexpected token at line %d: %s!", lineNumber, error));
	}
}
