package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.AbstractMapper;
import ftbsc.lll.mapper.IMapper;
import ftbsc.lll.mapper.tools.data.ClassData;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link IMapper} capable of parsing TSRG (an intermediary
 * format used by Forge) files.
 */

@AutoService(IMapper.class)
public class TSRGMapper extends AbstractMapper {

	/**
	 * Checks whether this mapper can process the given lines.
	 * @param lines the lines to read
	 * @return whether this type of mapper can process these lines
	 */
	@Override
	public boolean claim(List<String> lines) {
		return lines.get(0).startsWith("tsrg2 left right");
	}

	/**
	 * Reads the given lines of text and attempts to interpret them as
	 * mappings of the given type.
	 * @param lines the lines to read
	 * @param ignoreErrors try to ignore errors and keep going
	 * @throws MalformedMappingsException if an error is encountered and ignoreErrors is false
	 */
	@Override
	protected void processLines(List<String> lines, boolean ignoreErrors) throws MalformedMappingsException {
		//skip the first line ("tsrg2 left right")
		lines = new ArrayList<>(lines);
		lines.remove(0);

		String currentClass = "";
		for(String l : lines) {
			if(l == null) continue;
			if(l.startsWith("\t") || l.startsWith(" ")) {
				String[] split = l.trim().split(" ");
				if(split.length == 2) //field
					this.mappings.get(currentClass).addField(split[0], split[1]);
				else if (split.length == 3)//method
					this.mappings.get(currentClass).addMethod(split[0], split[2], split[1]); //add child
			} else {
				String[] sp = l.split(" ");
				ClassData s = new ClassData(sp[0], sp[1]);
				currentClass = s.name;
				this.mappings.put(s.name, s);
			}
		}
	}
}
