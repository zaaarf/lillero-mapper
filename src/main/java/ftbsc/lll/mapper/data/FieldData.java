package ftbsc.lll.mapper.data;

/**
 * Container class for method data.
 */
public class FieldData {
	/**
	 * The internal name of the parent class.
	 */
	public final ClassData parentClass;

	/**
	 * The name of the method.
	 */
	public final String name;

	/**
	 * The name mapped.
	 */
	public final String nameMapped;

	/**
	 * The field's type descriptor.
	 * Some formats may not specify it; if this was created in one such format,
	 * this is going to be null.
	 */
	public final String descriptor;

	/**
	 * Constructs a new {@link FieldData} with unspecified descriptor.
	 * @param parentClass the {@link ClassData} representation of the parent class
	 * @param name the field name
	 * @param nameMapped the mapped field name
	 */
	public FieldData(ClassData parentClass, String name, String nameMapped) {
		this.parentClass = parentClass;
		this.name = name;
		this.nameMapped = nameMapped;
		this.descriptor = null;
	}

	/**
	 * Constructs a new {@link FieldData} with descriptor.
	 * @param parentClass the {@link ClassData} representation of the parent class
	 * @param name the field name
	 * @param nameMapped the mapped field name
	 * @param descriptor the field's type descriptor
	 */
	public FieldData(ClassData parentClass, String name, String nameMapped, String descriptor) {
		this.parentClass = parentClass;
		this.name = name;
		this.nameMapped = nameMapped;
		this.descriptor = descriptor;
	}
}
