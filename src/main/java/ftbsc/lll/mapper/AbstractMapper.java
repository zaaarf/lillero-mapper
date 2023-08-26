package ftbsc.lll.mapper;

import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.mapper.tools.data.ClassData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A default implementation of {@link IMapper} meant to
 * recycle as much code as possible.
 */
public abstract class AbstractMapper implements IMapper {

	/**
	 * A {@link Map} tying each plain class name to its class data.
	 */
	protected final Map<String, ClassData> mappings = new HashMap<>();

	/**
	 * A {@link Map} tying each obfuscated name to its class data.
	 */
	protected final Map<String, ClassData> mappingsInverted = new HashMap<>();

	/**
	 * Populates the {@link IMapper} given the lines, ignoring errors depending on the
	 * given ignoreErrors flag.
	 * @param lines the lines to read
	 * @param ignoreErrors try to ignore errors and keep going
	 * @throws MalformedMappingsException if an error is encountered and ignoreErrors is false
	 */
	@Override
	public void populate(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		this.processLines(lines, ignoreErrors);
		this.mappings.forEach((name, data) -> {
			ClassData reverse = data.generateReverseMappings(this);
			this.mappingsInverted.put(data.nameMapped, reverse);
		});
	}

	/**
	 * Reads the given lines of text and attempts to interpret them as
	 * mappings of the given type.
	 * @param lines the lines to read
	 * @param ignoreErrors try to ignore errors and keep going
	 * @throws MalformedMappingsException if an error is encountered and ignoreErrors is false
	 */
	protected abstract void processLines(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException;

	/**
	 * Completely resets the mapper, clearing it of all existing mappings.
	 */
	@Override
	public void reset() {
		this.mappings.clear();
		this.mappingsInverted.clear();
	}

	/**
	 * Gets a name of a class from the given {@link Map}.
	 * @param name the name
	 * @param mappings the {@link Map} to pull data from
	 * @return the mapped name
	 * @throws MappingNotFoundException if no mapping is found
	 */
	private static String mapClass(String name, Map<String, ClassData> mappings) {
		ClassData data = mappings.get(name.replace('.', '/'));
		if(data == null)
			throw new MappingNotFoundException("class", name);
		else return data.nameMapped;
	}

	/**
	 * Gets the obfuscated name of the class.
	 * @param name the plain internal name of the desired class
	 * @return the obfuscated name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String obfuscateClass(String name)  {
		return mapClass(name, this.mappings);
	}

	/**
	 * Gets the plain name of the class.
	 * @param nameObf the obfuscated internal name of the desired class
	 * @return the plain name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String deobfuscateClass(String nameObf) throws MappingNotFoundException {
		return mapClass(nameObf, this.mappingsInverted);
	}

	/**
	 * Gets the name of a member from the given {@link Map}.
	 * @param parentName the parent class
	 * @param mappings the {@link Map} to pull data from
	 * @param memberName the field or method name
	 * @param methodDescriptor the method descriptor, may be null or partial
	 * @return the mapped member name
	 * @throws MappingNotFoundException if no mapping is found
	 */
	private static String mapMember(String parentName, Map<String, ClassData> mappings,
																	String memberName, String methodDescriptor) {
		ClassData data = mappings.get(parentName.replace('.', '/'));
		if(data == null)
			throw new MappingNotFoundException("class", parentName);

		if(methodDescriptor == null)
			return data.mapField(memberName).name;
		else return data.mapMethod(memberName, methodDescriptor).signature.name;
	}

	/**
	 * Gets the obfuscated name of a class member (field or method).
	 * @param parentName the unobfuscated internal name of the parent class
	 * @param memberName the field or method name
	 * @param methodDescriptor the optional descriptor of the member, may be null or partial
	 * @return the obfuscated name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String obfuscateMember(String parentName, String memberName, String methodDescriptor) {
		return mapMember(parentName, this.mappings, memberName, methodDescriptor);
	}

	/**
	 * Gets the plain name of a class member (field or method).
	 * @param parentName       the obfuscated internal name of the parent class
	 * @param memberName       the obfuscated field name or method signature
	 * @param methodDescriptor the obfuscated descriptor of the member (only for methods)
	 * @return the plain name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String deobfuscateMember(String parentName, String memberName, String methodDescriptor) throws MappingNotFoundException {
		return mapMember(parentName, this.mappingsInverted, memberName, methodDescriptor);
	}
}
