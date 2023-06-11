package ftbsc.lll.mapper.tools;

import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.mapper.IMapper;
import ftbsc.lll.tools.DescriptorBuilder;
import org.objectweb.asm.Type;

public class MappingUtils {
	/**
	 * Obfuscates a method descriptor, replacing its class references
	 * with their obfuscated counterparts.
	 * @param descriptor a {@link String} containing the descriptor
	 * @return the obfuscated descriptor
	 */
	public static String obfuscateMethodDescriptor(String descriptor, IMapper mapper) {
		Type method = Type.getMethodType(descriptor);
		Type[] arguments = method.getArgumentTypes();
		Type returnType = method.getReturnType();

		Type[] obfArguments = new Type[arguments.length];
		for(int i = 0; i < obfArguments.length; i++)
			obfArguments[i] = obfuscateType(arguments[i], mapper);

		return Type.getMethodDescriptor(obfuscateType(returnType, mapper), obfArguments);
	}

	/**
	 * Given a {@link Type} and a valid {@link IMapper} it returns its obfuscated
	 * counterpart.
	 * @param type the type in question
	 * @return the obfuscated type
	 */
	public static Type obfuscateType(Type type, IMapper mapper) {
		//unwrap arrays
		Type unwrapped = type;
		int arrayLevel = 0;
		while(unwrapped.getSort() == Type.ARRAY) {
			unwrapped = unwrapped.getElementType();
			arrayLevel++;
		}

		//if it's a primitive no operation is needed
		if(type.getSort() < Type.ARRAY)
			return type;

		String internalName = type.getInternalName();

		String internalNameObf;
		try {
			internalNameObf = mapper.obfuscateClass(internalName);
			return Type.getType(DescriptorBuilder.nameToDescriptor(internalNameObf, arrayLevel));
		} catch(MappingNotFoundException e) {
			return type;
		}
	}
}
