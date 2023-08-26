
package ftbsc.lll.mapper.tools.data;

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
	 * Constructs a new {@link FieldData}.
	 * @param parentClass the {@link ClassData} representation of the parent class
	 * @param name the field name
	 * @param nameMapped the mapped field name
	 */
	public FieldData(ClassData parentClass, String name, String nameMapped) {
		this.parentClass = parentClass;
		this.name = name;
		this.nameMapped = nameMapped;
	}
}
