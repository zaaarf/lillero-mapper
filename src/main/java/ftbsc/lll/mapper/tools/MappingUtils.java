package ftbsc.lll.mapper.tools;

import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.tools.DescriptorBuilder;
import org.objectweb.asm.Type;

/**
 * A collection of static utility methods correlated to
 * mappers.
 */
public class MappingUtils {

	/**
	 * Maps a method descriptor, replacing its class references with their mapped counterparts.
	 * @param descriptor a {@link String} containing the descriptor
	 * @param mapper the {@link Mapper} to use for the process
	 * @param reverse if true it uses the inverted mapper rather than the normal one
	 * @return the mapped descriptor
	 */
	public static String mapMethodDescriptor(String descriptor, Mapper mapper, boolean reverse) {
		Type method = Type.getMethodType(descriptor);
		Type[] arguments = method.getArgumentTypes();
		Type returnType = method.getReturnType();

		Type[] mappedArguents = new Type[arguments.length];
		for(int i = 0; i < mappedArguents.length; i++)
			mappedArguents[i] = mapType(arguments[i], mapper, reverse);

		return Type.getMethodDescriptor(mapType(returnType, mapper, reverse), mappedArguents);
	}

	/**
	 * Given a {@link Type} and a valid {@link Mapper} it returns its mapped counterpart.
	 * @param type the type in question
	 * @param mapper the {@link Mapper} to use for the process
	 * @param reverse if true it uses the inverted mapper rather than the normal one
	 * @return the mapped type
	 */
	public static Type mapType(Type type, Mapper mapper, boolean reverse) {
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

		String internalNameMapped;
		try {
			internalNameMapped = reverse
				? mapper.getInverted().getClassData(internalName).nameMapped
				: mapper.getClassData(internalName).nameMapped;
			return Type.getType(DescriptorBuilder.nameToDescriptor(internalNameMapped, arrayLevel));
		} catch(MappingNotFoundException e) {
			return type;
		}
	}
}
