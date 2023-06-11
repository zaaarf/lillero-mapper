package ftbsc.lll.mapper;

import ftbsc.lll.exceptions.MappingNotFoundException;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * A generic obfuscation mapper.
 */
public interface IMapper {

	/**
	 * Reads the given lines of text and attempts to interpret them as
	 * mappings of the given type.
	 * @param lines the lines to read
	 */
	void populate(Iterable<String> lines);

	/**
	 * Gets the obfuscated name of the class.
	 * @param name the unobfuscated internal name of the desired class
	 * @return the obfuscated name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	String obfuscateClass(String name);

	/**
	 * Gets the obfuscated name of a class member (field or method).
	 * @param parentName the unobfuscated internal name of the parent class
	 * @param memberName the field name or method signature
	 * @param methodDescriptor the descriptor of the member
	 * @return the obfuscated name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	String obfuscateMember(String parentName, String memberName, String methodDescriptor);

	/**
	 * Loads all valid parsers available in the classpath (via the Java Service API),
	 * attempts to parse the given lines into mappings, and returns all built mappers
	 * that succeeded without throwing errors or ftbsc.lll.exceptions.
	 * @param lines the lines of the mapping file
	 * @return a {@link Set} of mappers that could interpret the given input
	 */
	static Set<IMapper> getMappers(Iterable<String> lines) {
		Set<IMapper> parsed = new HashSet<>();
		for(IMapper mapper: ServiceLoader.load(IMapper.class)) {
			try {
				mapper.populate(lines);
				parsed.add(mapper);
			} catch(Throwable ignored) {}
		}
		return parsed;
	}
}
