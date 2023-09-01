package ftbsc.lll.mapper;

import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.tools.Mapper;

import java.util.List;

/**
 * The shared interface between all mappers.
 */
public interface IMappingFormat {
	/**
	 * Checks whether this mapper can process the given lines.
	 * @param lines the lines to read
	 * @return whether this type of mapper can process these lines
	 */
	boolean claim(List<String> lines);

	/**
	 * Defines a priority for this implementation: the higher the number,
	 * the higher the priority.
	 * This is used to resolve conflicts when multiple mappers attempt to
	 * {@link #claim(List) claim} a given mapping file.
	 * @return the priority
	 */
	default int priority() {
		return 0;
	}

	/**
	 * Creates a {@link Mapper} given the lines, ignoring errors depending on the given flag.
	 * @param lines the lines to read
	 * @param ignoreErrors try to ignore errors and keep going
	 * @return the {@link Mapper}
	 * @throws MalformedMappingsException if an error is encountered and ignoreErrors is false
	 */
	Mapper getMapper(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException;

	/**
	 * Creates a {@link Mapper} given the lines, ignoring errors depending on the given flag, and
	 * returns its inverted form.
	 * @param lines the lines to read
	 * @param ignoreErrors try to ignore errors and keep going
	 * @return the inverted {@link Mapper}
	 * @throws MalformedMappingsException if an error is encountered and ignoreErrors is false
	 */
	default Mapper getInvertedMapper(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		return this.getMapper(lines, ignoreErrors).getInverted();
	}
}
