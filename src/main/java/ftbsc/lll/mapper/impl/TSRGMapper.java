package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.IMappingFormat;
import ftbsc.lll.mapper.utils.Mapper;
import ftbsc.lll.mapper.data.ClassData;

import java.util.List;
import java.util.regex.Pattern;

/**
 * A {@link IMappingFormat} capable of parsing TSRG (an intermediary
 * format used by Forge) files.
 */

@AutoService(IMappingFormat.class)
public class TSRGMapper implements IMappingFormat {

	@Override
	public boolean claim(List<String> lines) {
		return Pattern.compile("tsrg2 [a-zA-Z]* [a-zA-Z]*")
			.matcher(lines.get(0)).matches();
	}

	@Override
	public Mapper getMapper(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		Mapper result = new Mapper();
		String currentClass = "";
		for(int i = 1; i < lines.size(); i++) { //start from 1 to skip header
			String currentLine = lines.get(i);
			boolean isMember = currentLine.startsWith("\t") || currentLine.startsWith(" ");
			String[] tokens = currentLine.trim().split(" ");
			if(isMember) {
				if(tokens.length == 2) //field
					result.getClassData(currentClass).addField(tokens[0], tokens[1]);
				else if(tokens.length == 3)//method
					result.getClassData(currentClass).addMethod(tokens[0], tokens[2], tokens[1]); //add child
				else if(!ignoreErrors) throw new MalformedMappingsException(i, "wrong number of space-separated tokens");
			} else {
				if(tokens.length == 2) {
					ClassData s = new ClassData(tokens[0], tokens[1]);
					currentClass = s.name;
					result.getRawMappings().put(s.name, s);
				} else if(!ignoreErrors) throw new MalformedMappingsException(i, "wrong number of space-separated tokens");
			}
		}
		return result;
	}
}
