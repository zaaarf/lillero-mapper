package ftbsc.lll.mapper.utils;

import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.mapper.data.ClassData;
import ftbsc.lll.mapper.data.FieldData;
import ftbsc.lll.mapper.data.MethodData;

import java.util.HashMap;
import java.util.Map;

/**
 * An object containing parsed mapping data, which can
 * apply a conversion as requested.
 */
public class Mapper {
	/**
	 * A {@link Map} tying each plain class name to its class data.
	 */
	protected final Map<String, ClassData> mappings = new HashMap<>();

	/**
	 * Gets the {@link ClassData} given the plain name.
	 * @param name the plain internal name of the desired class
	 * @return the mapped name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	public ClassData getClassData(String name) throws MappingNotFoundException {
		ClassData data = this.mappings.get(name.replace('.', '/'));
		if(data == null)
			throw new MappingNotFoundException("class", name);
		else return data;
	}

	/**
	 * Gets the mapped name of a method
	 * @param parent the plain internal name of the parent class
	 * @param name the plain method name
	 * @param descriptor the descriptor of the method
	 * @return the mapped name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	public MethodData getMethodData(String parent, String name, String descriptor) throws MappingNotFoundException {
		return this.getClassData(parent).mapMethod(name, descriptor);
	}

	/**
	 * Gets the mapped name of a field.
	 * @param parent the plain internal name of the parent class
	 * @param name the field's plain name
	 * @return the mapped name of the requested field
	 * @throws MappingNotFoundException if no mapping is found
	 */
	public FieldData getFieldData(String parent, String name) throws MappingNotFoundException {
		return this.getClassData(parent).mapField(name);
	}

	/**
	 * Gets the "raw mappings".
	 * @return a {@link Map} tying each {@link ClassData} to the class' plain name
	 */
	public Map<String, ClassData> getRawMappings() {
		return this.mappings;
	}

	/**
	 * Builds a new {@link Mapper} that functions in reverse to this one (i.e. one that
	 * considers as "mapped" what this one considers plain, and vice versa).
	 * @return the inverted mapper
	 */
	public Mapper getInverted() {
		Mapper inverted = new Mapper();
		this.mappings.forEach((name, data) -> {
			ClassData reverse = data.generateReverseMappings(this);
			inverted.mappings.put(data.nameMapped, reverse);
		});
		return inverted;
	}
}
