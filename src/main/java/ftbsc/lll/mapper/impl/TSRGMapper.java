package ftbsc.lll.mapper.impl;

import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.mapper.IMapper;
import ftbsc.lll.mapper.tools.ClassData;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses a .tsrg file into a mapper capable of converting from
 * plain names to obfuscated ones and vice versa.
 */
public class TSRGMapper implements IMapper {

	/**
	 * A Map containing the deobfuscated names as keys and information about
	 * each class as values.
	 */
	private final Map<String, ClassData> mappings = new HashMap<>();

	/**
	 * Reads the given lines of text and attempts to interpret them as
	 * mappings of the given type.
	 * @param lines the lines to read
	 */
	@Override
	public void populate(Iterable<String> lines) {
		String currentClass = "";
		for(String l : lines) {
			if(l == null) continue;
			if(l.startsWith("\t"))
				mappings.get(currentClass).addMember(l);
			else {
				String[] sp = l.split(" ");
				ClassData s = new ClassData(sp[0], sp[1]);
				currentClass = s.unobf;
				mappings.put(s.unobf, s);
			}
		}
	}

	/**
	 * Gets the obfuscated name of the class.
	 * @param name the unobfuscated internal name of the desired class
	 * @return the obfuscated name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String obfuscateClass(String name)  {
		ClassData data = mappings.get(name.replace('.', '/'));
		if(data == null)
			throw new MappingNotFoundException(name);
		else return data.obf;
	}

	/**
	 * Gets the obfuscated name of a class member (field or method).
	 * @param parentName the unobfuscated internal name of the parent class
	 * @param memberName the field name or method signature
	 * @param methodDescriptor the optional descriptor of the member, may be null or partial
	 * @return the obfuscated name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String obfuscateMember(String parentName, String memberName, String methodDescriptor) {
		ClassData data = mappings.get(parentName.replace('.', '/'));
		if(data == null)
			throw new MappingNotFoundException(parentName + "::" + memberName);
		return data.get(memberName, methodDescriptor);
	}
}
