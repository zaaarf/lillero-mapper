package ftbsc.lll.exceptions;

public class MalformedMappingsException extends Exception {
	public MalformedMappingsException(String mapping, String type) {
		super(String.format("Unexpected token at line %s for mapper type %s!", mapping, type));
	}
}
