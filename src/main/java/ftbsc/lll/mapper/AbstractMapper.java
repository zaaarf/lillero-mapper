package ftbsc.lll.mapper;

import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.mapper.tools.data.ClassData;
import ftbsc.lll.mapper.tools.data.FieldData;
import ftbsc.lll.mapper.tools.data.MethodData;

import java.util.HashMap;
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

	@Override
	public void reset() {
		this.mappings.clear();
	}

	@Override
	public IMapper getInverted() {
		AbstractMapper inverted = newInstance();
		this.mappings.forEach((name, data) -> {
			ClassData reverse = data.generateReverseMappings(this);
			inverted.mappings.put(data.nameMapped, reverse);
		});
		return inverted;
	}

	/**
	 * Creates a new instance of this type of mapper.
	 * @return the new, empty instance
	 */
	protected abstract AbstractMapper newInstance();


	@Override
	public ClassData getClassData(String name) throws MappingNotFoundException {
		ClassData data = this.mappings.get(name.replace('.', '/'));
		if(data == null)
			throw new MappingNotFoundException("class", name);
		else return data;
	}

	@Override
	public MethodData getMethodData(String parent, String name, String descriptor) throws MappingNotFoundException {
		return this.getClassData(parent).mapMethod(name, descriptor);
	}

	@Override
	public FieldData getFieldData(String parent, String name) throws MappingNotFoundException {
		return this.getClassData(parent).mapField(name);
	}

	@Override
	public Map<String, ClassData> getRawMappings() {
		return this.mappings;
	}
}
