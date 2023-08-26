package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.exceptions.MappingNotFoundException;
import ftbsc.lll.mapper.IMapper;
import ftbsc.lll.mapper.MapperProvider;
import ftbsc.lll.mapper.tools.MappingUtils;

import java.util.ArrayList;
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

	/**
	 * Checks whether this mapper can process the given lines.
	 * @param lines the lines to read
	 * @return whether this type of mapper can process these lines
	 */
	@Override
	public boolean claim(List<String> lines) {
		return lines.get(0).equals("lll multimapper");
	}

	/**
	 * Populates the {@link IMapper} given the lines, ignoring errors depending on the
	 * given ignoreErrors flag.
	 * @param lines the lines to read
	 * @param ignoreErrors try to ignore errors and keep going
	 * @throws MalformedMappingsException if an error is encountered and ignoreErrors is false
	 */
	@Override
	public void populate(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		for(int i = 1; i < lines.size(); i++) {
			List<String> data = MapperProvider.fetchFromLocalOrRemote(lines.get(i));
			IMapper mapper = MapperProvider.getMapper(data);
			mapper.populate(data, ignoreErrors);
			this.mapperList.add(mapper);
		}
	}

	/**
	 * Completely resets the mapper, clearing it of all existing mappings.
	 */
	@Override
	public void reset() {
		this.mapperList.forEach(IMapper::reset);
		this.mapperList.clear();
	}

	/**
	 * Gets the obfuscated name of the class.
	 * @param name the plain internal name of the desired class
	 * @return the obfuscated name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String obfuscateClass(String name) throws MappingNotFoundException {
		for(IMapper mapper : this.mapperList)
			name = mapper.obfuscateClass(name);
		return name;
	}

	/**
	 * Gets the plain name of the class.
	 * @param nameObf the obfuscated internal name of the desired class
	 * @return the plain name of the class
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String deobfuscateClass(String nameObf) throws MappingNotFoundException {
		for(int i = this.mapperList.size() - 1; i >= 0; i--)
			nameObf = this.mapperList.get(i).deobfuscateClass(nameObf);
		return nameObf;
	}

	/**
	 * Gets the obfuscated name of a class member (field or method).
	 * @param parentName the plain internal name of the parent class
	 * @param memberName the field name or method signature
	 * @param methodDescriptor the descriptor of the member (only for methods)
	 * @return the obfuscated name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String obfuscateMember(String parentName, String memberName, String methodDescriptor) throws MappingNotFoundException {
		for(IMapper mapper : this.mapperList) {
			memberName = mapper.obfuscateMember(parentName, memberName, methodDescriptor);
			methodDescriptor = MappingUtils.mapMethodDescriptor(methodDescriptor, mapper, false);
			parentName = mapper.obfuscateClass(parentName);
		}
		return memberName;
	}

	/**
	 * Gets the plain name of a class member (field or method).
	 * @param parentName the obfuscated internal name of the parent class
	 * @param memberName the obfuscated field name or method signature
	 * @param methodDescriptor the obfuscated descriptor of the member (only for methods)
	 * @return the plain name of the given member
	 * @throws MappingNotFoundException if no mapping is found
	 */
	@Override
	public String deobfuscateMember(String parentName, String memberName, String methodDescriptor) throws MappingNotFoundException {
		for(int i = this.mapperList.size() - 1; i >= 0; i--) {
			IMapper mapper = this.mapperList.get(i);
			memberName = mapper.deobfuscateMember(parentName, memberName, methodDescriptor);
			methodDescriptor = MappingUtils.mapMethodDescriptor(methodDescriptor, mapper, true);
			parentName = mapper.deobfuscateClass(parentName);
		}
		return memberName;
	}
}
