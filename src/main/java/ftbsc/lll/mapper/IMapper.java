package ftbsc.lll.mapper;

import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.mapper.tools.data.ClassData;
import ftbsc.lll.mapper.tools.data.FieldData;
import ftbsc.lll.mapper.tools.data.MethodData;

import java.util.*;

/**
 * The shared interface between all mappers.
 */
public interface IMapper {
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
	 * Populates the {@link IMapper} given the lines, ignoring errors depending on the
	 * given ignoreErrors flag.
	 * @param lines the lines to read
	 * @param ignoreErrors try to ignore errors and keep going
	 * @throws MalformedMappingsException if an error is encountered and ignoreErrors is false
	 */
	void populate(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException;

	/**
	 * Builds an {@link IMapper} that functions in reverse to this one (i.e. one that
	 * considers as "mapped" what this one considers plain, and vice versa).
	 * @return the inverted mapper
	 */
	IMapper getInverted();

	/**
	 * Completely resets the mapper, clearing it of all existing mappings.
	 */
	void reset();

	/**
	 * Gets the {@link ClassData}
	 * @param name the plain internal name of the desired class
	 * @return the obfuscated name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	ClassData getClassData(String name) throws MappingNotFoundException;

	/**
	 * Gets the obfuscated name of a class member (field or method).
	 * @param parent the plain internal name of the parent class
	 * @param name the field name
	 * @param descriptor the descriptor of the member (only for methods)
	 * @return the obfuscated name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	MethodData getMethodData(String parent, String name, String descriptor) throws MappingNotFoundException;

	/**
	 * Gets the obfuscated name of a class member (field or method).
	 * @param parent the plain internal name of the parent class
	 * @param name the field name
	 * @return the obfuscated name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	FieldData getFieldData(String parent, String name) throws MappingNotFoundException;
}
