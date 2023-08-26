package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.AbstractMapper;
import ftbsc.lll.mapper.IMapper;
import ftbsc.lll.mapper.tools.data.ClassData;

import java.util.List;

/**
 * A {@link IMapper} capable of parsing SRG files.
 */
@AutoService(IMapper.class)
public class SRGMapper extends AbstractMapper {
	/**
	 * Checks whether this mapper can process the given lines.
	 * @param lines the lines to read
	 * @return whether this type of mapper can process these lines
	 */
	@Override
	public boolean claim(List<String> lines) {
		String[] firstLineTokens = lines.get(0).trim().split(" ");
		return firstLineTokens.length <= 5 &&
			(firstLineTokens[0].equals("CL:")
				|| firstLineTokens[0].equals("MD:")
				|| firstLineTokens[0].equals("FD:"));
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
		for(int i = 0; i < lines.size(); i++) {
			String[] tokens = lines.get(i).trim().split(" ");
			switch(tokens[0]) {
				case "CL:":
					if(tokens.length != 3)
						break;
					this.registerClass(tokens[1], tokens[2]);
					continue;
				case "MD:":
				case "FD:":
					if(this.processMemberTokens(tokens))
						continue;
					break;
			}
			if(!ignoreErrors)
				throw new MalformedMappingsException(i, "wrong number of space-separated tokens");
		}
	}

	/**
	 * Processes a line, broken up into tokens.
	 * @param tokens the tokens
	 * @return whether it was a valid mapping
	 */
	private boolean processMemberTokens(String[] tokens) {
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
		if(field) this.registerMember(parent, parentObf, memberName, memberNameObf, null, null);
		else this.registerMember(parent, parentObf, memberName, memberNameObf, tokens[2], tokens[4]);
		return true;
	}

	/**
	 * This does nothing as it is never called for this type of mapper.
	 * @param lines the lines to read
	 * @param ignoreErrors try to ignore errors and keep going
	 */
	@Override
	protected void processLines(List<String> lines, boolean ignoreErrors) {}

	/**
	 * Registers a class in the mapper, if it isn't already.
	 * @param name the name
	 * @param nameMapped the mapped name
	 */
	private void registerClass(String name, String nameMapped) {
		if(this.mappings.containsKey(name))
			return;
		this.mappings.put(name, new ClassData(name, nameMapped));
		this.mappingsInverted.put(nameMapped, new ClassData(nameMapped, name));
	}

	/**
	 * Registers a class member. The descriptors should be null for fields.
	 * @param parent the parent's plain internal name
	 * @param parentMapped the parent's mapped internal name
	 * @param name the member's plain name
	 * @param nameMapped the member's mapped name
	 * @param descriptor the member's plain descriptor, may be null
	 * @param descriptorMapped the member's mapped descriptor, may be null
	 */
	private void registerMember(String parent, String parentMapped, String name, String nameMapped,
															String descriptor, String descriptorMapped) {
		this.registerClass(parent, parentMapped);
		ClassData data = this.mappings.get(parent);
		ClassData dataReverse = this.mappings.get(data.nameMapped);
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
}