package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.IMappingFormat;
import ftbsc.lll.mapper.MapperProvider;
import ftbsc.lll.mapper.tools.Mapper;
import ftbsc.lll.mapper.tools.MappingUtils;
import ftbsc.lll.mapper.tools.data.ClassData;

import java.util.ArrayList;
import java.util.List;

/**
 * Special mapper type that actually resolves to an ordered
 * sequence of mappers applied one after the other.
 */
@AutoService(IMappingFormat.class)
public class MultiMapper implements IMappingFormat {
	@Override
	public boolean claim(List<String> lines) {
		return lines.get(0).equals("lll multimapper");
	}

	@Override
	public Mapper getMapper(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		Mapper result = new Mapper();
		List<Mapper> mapperList = new ArrayList<>();

		for(int i = 1; i < lines.size(); i++) {
			List<String> data = MapperProvider.fetchFromLocalOrRemote(lines.get(i));
			IMappingFormat format = MapperProvider.getMapper(data);
			mapperList.add(format.getMapper(data, ignoreErrors));
		}

		mapperList.get(0).getRawMappings().forEach((name, data) -> {
			ClassData finalData = data;
			for(int i = 1; i < mapperList.size(); i++)
				finalData = mapperList.get(i).getClassData(finalData.nameMapped);

			ClassData sumData = new ClassData(data.name, finalData.nameMapped);

			data.getMethods().forEach((signature, methodData) -> {
				for(int i = 1; i < mapperList.size(); i++) {
					Mapper mapper = mapperList.get(i);
					methodData = mapper.getMethodData(methodData.parentClass.nameMapped, methodData.nameMapped,
						MappingUtils.mapMethodDescriptor(methodData.signature.descriptor, mapper, false));
				}
				sumData.addMethod(signature.name, methodData.nameMapped, signature.descriptor);
			});

			data.getFields().forEach((fieldName, fieldData) -> {
				for(int i = 1; i < mapperList.size(); i++)
					fieldData = mapperList.get(i).getFieldData(fieldData.parentClass.nameMapped, fieldData.nameMapped);
				sumData.addField(fieldName, fieldData.nameMapped, fieldData.descriptor);
			});

			result.getRawMappings().put(sumData.name, sumData);
		});

		return result;
	}
}
