package ftbsc.lll.exceptions;

import ftbsc.lll.mapper.IMapper;

/**
 * Thrown upon failure to find the requested mapping within a loaded {@link IMapper}.
 */
public class MappingNotFoundException extends RuntimeException {

	/**
	 * Constructs a new mapping not found exception for the specified mapping.
	 * @param type the type of mapping
	 * @param mapping the relevant mapping
	 */
	public MappingNotFoundException(String type, String mapping) {
		super(String.format("Could not find mapping for %s %s!", type, mapping));
	}
}
