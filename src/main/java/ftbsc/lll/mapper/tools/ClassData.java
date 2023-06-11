package ftbsc.lll.mapper.tools;

import ftbsc.lll.exceptions.AmbiguousMappingException;
import ftbsc.lll.exceptions.MappingNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Container class used to store information about classes.
 */
public class ClassData {
	/**
	 * The unobfuscated name (FQN with '/' instad of '.') of the class.
	 */
	public final String unobf;

	/**
	 * The obfuscated internal name (FQN with '/' instad of '.') of the class.
	 */
	public final String obf;

	/**
	 * A {@link Map} tying each member's name or signature to its
	 * obfuscated counterpart.
	 */
	public final Map<String, String> members;

	/**
	 * The constructor. It takes in the names (obfuscated and non-obfuscated)
	 * of a class.
	 * @param unobf the unobfuscated name
	 * @param obf the obfuscated name
	 */
	public ClassData(String unobf, String obf) {
		this.unobf = unobf;
		this.obf = obf;
		this.members = new HashMap<>();
	}

	/**
	 * Adds a member to the target class.
	 * For fields only the names are required; for methods,
	 * this takes in the full signature ({@code name + " " + space}).
	 * @param s the String representing the declaration line
	 */
	public void addMember(String s) {
		String[] split = s.trim().split(" ");
		if(split.length == 2) //field
			members.put(split[0], split[1]);
		else if (split.length == 3) //method
			members.put(split[0] + " " + split[1], split[2]);
	}

	/**
	 * Gets an obfuscated member given the method name and a method descriptor,
	 * which may be partial (i.e. not include return type) or null if the member
	 * is not a method.
	 * @param memberName member name
	 * @param methodDescriptor the method descriptor, or null if it's not a method
	 * @return the requested obfuscated name, or null if nothing was found
	 * @throws AmbiguousMappingException if not enough data was given to uniquely identify a mapping
	 */
	public String get(String memberName, String methodDescriptor) {

		//find all keys that start with the name
		List<String> candidates = members.keySet().stream().filter(
			m -> m.split(" ")[0].equals(memberName)
		).collect(Collectors.toList());

		if(methodDescriptor != null) {
			String signature = String.format("%s %s", memberName, methodDescriptor);
			candidates = candidates.stream().filter(
				m -> m.equals(signature)
			).collect(Collectors.toList());
		}

		switch(candidates.size()) {
			case 0:
				throw new MappingNotFoundException(String.format(
					"%s.%s%s",
					this.unobf,
					memberName,
					methodDescriptor == null ? "" : "()"
				));
			case 1:
				return members.get(candidates.get(0));
			default:
				throw new AmbiguousMappingException(String.format(
					"Mapper could not uniquely identify member %s.%s%s, found %d!",
					this.unobf,
					memberName,
					methodDescriptor == null ? "" : "()",
					candidates.size()
				));
		}
	}
}