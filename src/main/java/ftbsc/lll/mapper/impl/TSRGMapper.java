package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.AbstractMapper;
import ftbsc.lll.mapper.IMapper;
import ftbsc.lll.mapper.tools.data.ClassData;

import java.util.List;

/**
 * A {@link IMapper} capable of parsing TSRG (an intermediary
 * format used by Forge) files.
 */

@AutoService(IMapper.class)
public class TSRGMapper extends AbstractMapper {

	@Override
	public boolean claim(List<String> lines) {
		return lines.get(0).startsWith("tsrg2 left right");
	}

	@Override
	public void populate(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		String currentClass = "";
		for(int i = 1; i < lines.size(); i++) { //start from 1 to skip header
			String currentLine = lines.get(i);
			boolean isMember = currentLine.startsWith("\t") || currentLine.startsWith(" ");
			String[] tokens = currentLine.trim().split(" ");
			if(isMember) {
				if(tokens.length == 2) //field
					this.mappings.get(currentClass).addField(tokens[0], tokens[1]);
				else if(tokens.length == 3)//method
					this.mappings.get(currentClass).addMethod(tokens[0], tokens[2], tokens[1]); //add child
				else if(!ignoreErrors) throw new MalformedMappingsException(i, "wrong number of space-separated tokens");
			} else {
				if(tokens.length == 2) {
					ClassData s = new ClassData(tokens[0], tokens[1]);
					currentClass = s.name;
					this.mappings.put(s.name, s);
				} else if(!ignoreErrors) throw new MalformedMappingsException(i, "wrong number of space-separated tokens");
			}
		}
	}

	@Override
	protected AbstractMapper newInstance() {
		return new TSRGMapper();
	}
}
