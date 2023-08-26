package ftbsc.lll.mapper.tools.data;

import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.mapper.IMapper;
import ftbsc.lll.mapper.tools.MappingUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Container class used to store information about classes.
 */
public class ClassData {

	/**
	 * The internal (like the fully-qualified name, but with '/' instead
	 * of '.') of the class.
	 */
	public final String name;

	/**
	 * The mapped internal (like the fully-qualified name, but with '/'
	 * instead of '.') of the class.
	 */
	public final String nameMapped;

	/**
	 * A {@link Map} tying each method's signature to its data class.
	 */
	private final Map<MethodSignature, MethodData> methods;

	/**
	 * A {@link Map} tying each field's name to its data class.
	 */
	private final Map<String, FieldData> fields;

	/**
	 * The constructor. It takes in the names (plain and mapped) of a class.
	 * @param name the plain name
	 * @param nameMapped the mapped name
	 */
	public ClassData(String name, String nameMapped) {
		this.name = name;
		this.nameMapped = nameMapped;
		this.methods = new HashMap<>();
		this.fields = new HashMap<>();
	}

	/**
	 * Adds a method to the target class.
	 * @param name the method name
	 * @param nameMapped the mapped method name
	 * @param descriptor the descriptor of the method
	 */
	public void addMethod(String name, String nameMapped, String descriptor) {
		MethodData data = new MethodData(this, name, nameMapped, descriptor);
		this.methods.put(data.signature, data);
	}

	/**
	 * Adds a field to the target class.
	 * @param plain the name of the field
	 * @param mapped the mapped name of the field
	 */
	public void addField(String plain, String mapped) {
		this.fields.put(plain, new FieldData(this, plain, mapped));
	}

	/**
	 * Generates the reverse mappings for this class.
	 * Should always be called only after the given mapper has finished
	 * processing all classes.
	 * @param mapper the mapper that generated this data
	 */
	public ClassData generateReverseMappings(IMapper mapper) {
		ClassData reverse = new ClassData(this.nameMapped, this.name);
		this.methods.forEach((signature, data) -> reverse.addMethod(nameMapped, signature.name,
			MappingUtils.mapMethodDescriptor(signature.descriptor, mapper, false)));
		this.fields.forEach((name, data) -> reverse.addField(data.nameMapped, name));
		return reverse;
	}

	/**
	 * Gets the {@link MethodData} from its name and descriptor, which may be partial
	 * (i.e. not include the return type).
	 * @param methodName the method name
	 * @param methodDescriptor the method descriptor, which may be partial
	 * @return the requested {@link MethodData}
	 * @throws MappingNotFoundException if the mapping wasn't found
	 */
	public MethodData mapMethod(String methodName, String methodDescriptor) {
		List<MethodSignature> signatures = this.methods.keySet().stream().filter(
			s -> s.name.equals(methodName) && s.descriptor.startsWith(methodDescriptor)
		).collect(Collectors.toList());
		if(signatures.size() > 1)
			throw new RuntimeException(); //should never happen unless something goes horribly wrong
		else if(signatures.isEmpty())
			throw new MappingNotFoundException("method",
				String.format("%s::%s%s", this.name, methodName, methodDescriptor));
		return this.methods.get(signatures.get(0));
	}

	/**
	 * Gets the {@link FieldData} its name.
	 * @param fieldName the field name
	 * @return the requested {@link FieldData}
	 * @throws MappingNotFoundException if the mapping wasn't found
	 */
	public FieldData mapField(String fieldName) {
		FieldData data = this.fields.get(fieldName);
		if(data == null)
			throw new MappingNotFoundException("field", String.format("%s.%s", this.name, fieldName));
		else return data;
	}
}