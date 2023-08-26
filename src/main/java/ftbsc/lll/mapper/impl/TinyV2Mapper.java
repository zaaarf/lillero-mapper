package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.AbstractMapper;
import ftbsc.lll.mapper.IMapper;
import ftbsc.lll.mapper.tools.data.ClassData;

import java.util.List;
import java.util.regex.Pattern;

/**
 * A {@link IMapper} capable of parsing TinyV2 mappings
 */
@AutoService(IMapper.class)
public class TinyV2Mapper extends AbstractMapper {

	@Override
	public boolean claim(List<String> lines) {
		return Pattern.compile("tiny\t2\t[0-9]\t[a-zA-Z]*\t[a-zA-Z]*")
			.matcher(lines.get(0)).matches();
	}

	@Override
	public void populate(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		String currentClass = "";
		for(int i = 1; i < lines.size(); i++) {
			String currentLine = lines.get(i);
			String[] tokens = currentLine.trim().split("\t");
			int tabCount = currentLine.indexOf(tokens[0]); //get number of leading tabs
			switch(tabCount) {
				case 0: //classes
					if(tokens.length == 3) {
						if(tokens[0].charAt(0) == 'c') {
							this.mappings.put(tokens[1], new ClassData(tokens[1], tokens[2]));
							currentClass = tokens[1];
						} else if(!ignoreErrors)
							throw new MalformedMappingsException(i, "root-level element must be class");
						continue;
					}
					break;
				case 1: //class members
					if(currentClass.isEmpty()) {
						if(ignoreErrors) continue;
						else throw new MalformedMappingsException(i, "class member without parent class");
					}
					switch(tokens[0].charAt(0)) {
						case 'm': //methods
							if(tokens.length == 4)
								break;
							this.mappings.get(currentClass).addMethod(tokens[2], tokens[3], tokens[1]);
							continue;
						case 'f': //fields
							if(tokens.length == 4)
								break;
							this.mappings.get(currentClass).addField(tokens[2], tokens[3]);
							continue;
					}
					break;
				case 2: //parameters, our mappers don't really support those
					break;
			}
			if(!ignoreErrors)
				throw new MalformedMappingsException(i, "wrong number of space-separated tokens");
		}
	}

	@Override
	protected AbstractMapper newInstance() {
		return new TinyV2Mapper();
	}
}
