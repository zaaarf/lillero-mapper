package ftbsc.lll.mapper;

import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.exceptions.MappingNotFoundException;

import java.util.*;

/**
 * A generic obfuscation mapper.
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
	 * Completely resets the mapper, clearing it of all existing mappings.
	 */
	void reset();

	/**
	 * Gets the obfuscated name of the class.
	 * @param name the plain internal name of the desired class
	 * @return the obfuscated name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	String obfuscateClass(String name) throws MappingNotFoundException;

	/**
	 * Gets the plain name of the class.
	 * @param nameObf the obfuscated internal name of the desired class
	 * @return the plain name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	String deobfuscateClass(String nameObf) throws MappingNotFoundException;

	/**
	 * Gets the obfuscated name of a class member (field or method).
	 * @param parentName the plain internal name of the parent class
	 * @param memberName the field name or method signature
	 * @param methodDescriptor the descriptor of the member (only for methods)
	 * @return the obfuscated name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	String obfuscateMember(String parentName, String memberName, String methodDescriptor) throws MappingNotFoundException;

	/**
	 * Gets the plain name of a class member (field or method).
	 * @param parentName the obfuscated internal name of the parent class
	 * @param memberName the obfuscated field name or method signature
	 * @param methodDescriptor the obfuscated descriptor of the member (only for methods)
	 * @return the plain name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	String deobfuscateMember(String parentName, String memberName, String methodDescriptor) throws MappingNotFoundException;

}
