package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.IMappingFormat;
import ftbsc.lll.mapper.utils.Mapper;
import ftbsc.lll.mapper.data.ClassData;

import java.util.List;

/**
 * A {@link IMappingFormat} capable of parsing SRG mappings.
 */
@AutoService(IMappingFormat.class)
public class SRGMapper implements IMappingFormat {

	@Override
	public boolean claim(List<String> lines) {
		String[] firstLineTokens = lines.get(0).trim().split(" ");
		return firstLineTokens.length <= 5 &&
			(firstLineTokens[0].equals("CL:")
				|| firstLineTokens[0].equals("MD:")
				|| firstLineTokens[0].equals("FD:"));
	}

	/**
	 * Builds the two mappers, and returns one of the two depending on the flag.
	 * Since the SRG format contains descriptor mappings, it's possible to process
	 * this right away.
	 * @param lines the lines to read
	 * @param ignoreErrors try to ignore errors and keep going
	 * @param inverted whether it should return the inverted one
	 * @return the {@link Mapper}, inverted depending on the flag
	 * @throws MalformedMappingsException if an error is encountered and ignoreErrors is false
	 */
	protected Mapper buildMapper(List<String> lines, boolean ignoreErrors, boolean inverted) throws MalformedMappingsException {
		Mapper mapper = new Mapper();
		Mapper invertedMapper = new Mapper();
		for(int i = 0; i < lines.size(); i++) {
			String[] tokens = lines.get(i).trim().split(" ");
			switch(tokens[0]) {
				case "CL:":
					if(tokens.length != 3)
						break;
					this.registerClass(mapper, invertedMapper, tokens[1], tokens[2]);
					continue;
				case "MD:":
				case "FD:":
					if(this.processMemberTokens(mapper, invertedMapper, tokens))
						continue;
					break;
			}
			if(!ignoreErrors)
				throw new MalformedMappingsException(i, "wrong number of space-separated tokens");
		}
		return inverted ? invertedMapper : mapper;
	}

	/**
	 * Processes a line, broken up into tokens.
	 * @param mapper the {@link Mapper} with normal mappings
	 * @param invertedMapper the {@link Mapper} with inverted mappings
	 * @param tokens the tokens
	 * @return whether it was a valid mapping
	 */
	private boolean processMemberTokens(Mapper mapper, Mapper invertedMapper, String[] tokens) {
		boolean field;
		if(tokens[0].equals("MD:")) {
			if(tokens.length != 5)
				return false;
			field = false;
		} else if(tokens[0].equals("FD:")) {
			if(tokens.length != 3)
				return false;
			field = true;
		} else return false;
		//process parent
		String[] split = tokens[1].split("/");
		String memberName = split[split.length - 1];
		String parent = tokens[1].substring(0, tokens[1].length() - split[split.length - 1].length() - 1);
		int obfPosition = field ? 2 : 3;
		split = tokens[obfPosition].split("/");
		String memberNameObf = split[split.length - 1];
		String parentObf = tokens[obfPosition].substring(0, tokens[obfPosition].length() - split[split.length - 1].length() - 1);
		this.registerMember(mapper, invertedMapper, parent, parentObf, memberName, memberNameObf,
			field ? null : tokens[2], field ? null : tokens[4]);
		return true;
	}

	/**
	 * Registers a class in the mapper, if it isn't already.
	 * @param mapper the {@link Mapper} with normal mappings
	 * @param invertedMapper the {@link Mapper} with inverted mappings
	 * @param name the name
	 * @param nameMapped the mapped name
	 */
	private void registerClass(Mapper mapper, Mapper invertedMapper, String name, String nameMapped) {
		if(mapper.getRawMappings().containsKey(name))
			return;
		mapper.getRawMappings().put(name, new ClassData(name, nameMapped));
		invertedMapper.getRawMappings().put(nameMapped, new ClassData(nameMapped, name));
	}

	/**
	 * Registers a class member. The descriptors should be null for fields.
	 * @param mapper the {@link Mapper} with normal mappings
	 * @param invertedMapper the {@link Mapper} with inverted mappings
	 * @param parent the parent's plain internal name
	 * @param parentMapped the parent's mapped internal name
	 * @param name the member's plain name
	 * @param nameMapped the member's mapped name
	 * @param descriptor the member's plain descriptor, may be null
	 * @param descriptorMapped the member's mapped descriptor, may be null
	 */
	private void registerMember(Mapper mapper, Mapper invertedMapper, String parent,
															String parentMapped, String name, String nameMapped,
															String descriptor, String descriptorMapped) {
		this.registerClass(mapper, invertedMapper, parent, parentMapped);
		ClassData data = mapper.getClassData(parent);
		ClassData dataReverse = invertedMapper.getClassData(data.nameMapped);
		if(descriptor == null || descriptorMapped == null) {
			//field
			data.addField(name, nameMapped);
			dataReverse.addField(nameMapped, name);
		} else {
			//method
			data.addMethod(name, nameMapped, descriptor);
			dataReverse.addMethod(nameMapped, name, descriptorMapped);
		}
	}

	@Override
	public Mapper getMapper(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		return this.buildMapper(lines, ignoreErrors, false);
	}

	@Override
	public Mapper getInvertedMapper(List<String> lines, boolean ignoreErrors) {
		return this.buildMapper(lines, ignoreErrors, true);
	}
}
