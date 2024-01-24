package ftbsc.lll.mapper.data;

/**
 * Container class for method data.
 */
public class MethodData {

	/**
	 * The internal name of the parent class.
	 */
	public final ClassData parentClass;

	/**
	 * The signature of the method.
	 */
	public final MethodSignature signature;

	/**
	 * The mapped name of the method.
	 */
	public final String nameMapped;

	/**
	 * Constructs a new {@link MethodData}.
	 * @param parentClass the {@link ClassData} representation of the parent class
	 * @param name the method name
	 * @param nameMapped the mapped method name
	 * @param descriptor the method's descriptor
	 */
	public MethodData(ClassData parentClass, String name, String nameMapped, String descriptor) {
		this.parentClass = parentClass;
		this.signature = new MethodSignature(name, descriptor);
		this.nameMapped = nameMapped;
	}
}
