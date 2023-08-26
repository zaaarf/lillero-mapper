package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.mapper.IMapper;
import ftbsc.lll.mapper.MapperProvider;
import ftbsc.lll.mapper.tools.MappingUtils;
import ftbsc.lll.mapper.tools.data.ClassData;
import ftbsc.lll.mapper.tools.data.FieldData;
import ftbsc.lll.mapper.tools.data.MethodData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Special mapper type that actually resolves to an ordered
 * sequence of mappers applied one after the other.
 */
@AutoService(IMapper.class)
public class MultiMapper implements IMapper {

	/**
	 * The list of mappers.
	 */
	private final List<IMapper> mapperList = new ArrayList<>();

	@Override
	public boolean claim(List<String> lines) {
		return lines.get(0).equals("lll multimapper");
	}

	@Override
	public void populate(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		for(int i = 1; i < lines.size(); i++) {
			List<String> data = MapperProvider.fetchFromLocalOrRemote(lines.get(i));
			IMapper mapper = MapperProvider.getMapper(data);
			mapper.populate(data, ignoreErrors);
			this.mapperList.add(mapper);
		}
	}

	@Override
	public IMapper getInverted() {
		MultiMapper reverse = new MultiMapper();
		this.mapperList.forEach(m -> reverse.mapperList.add(m.getInverted()));
		Collections.reverse(reverse.mapperList);
		return reverse;
	}

	@Override
	public void reset() {
		this.mapperList.forEach(IMapper::reset);
		this.mapperList.clear();
	}

	@Override
	public ClassData getClassData(String name) throws MappingNotFoundException {
		ClassData classData = this.mapperList.get(0).getClassData(name);
		for(int i = 1; i < this.mapperList.size(); i++)
			classData = this.mapperList.get(i).getClassData(classData.nameMapped);
		return classData;
	}

	@Override
	public MethodData getMethodData(String parent, String name, String descriptor) throws MappingNotFoundException {
		MethodData methodData = this.mapperList.get(0).getMethodData(parent, name, descriptor);
		for(int i = 1; i < this.mapperList.size(); i++) {
			IMapper mapper = this.mapperList.get(i);
			methodData = mapper.getMethodData(methodData.parentClass.nameMapped, methodData.nameMapped,
				MappingUtils.mapMethodDescriptor(methodData.signature.descriptor, mapper, false));
		}
		return methodData;
	}

	@Override
	public FieldData getFieldData(String parent, String name) throws MappingNotFoundException {
		FieldData fieldData = this.mapperList.get(0).getFieldData(parent, name);
		for(int i = 1; i < this.mapperList.size(); i++)
			fieldData = this.mapperList.get(i).getFieldData(fieldData.parentClass.nameMapped, fieldData.nameMapped);
		return fieldData;
	}
}
