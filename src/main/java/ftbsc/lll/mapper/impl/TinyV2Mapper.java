package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.IMappingFormat;
import ftbsc.lll.mapper.utils.Mapper;
import ftbsc.lll.mapper.data.ClassData;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link IMappingFormat} capable of parsing TinyV2 mappings.
 */
@AutoService(IMappingFormat.class)
public class TinyV2Mapper implements IMappingFormat {
	private static final Pattern HEADER_REGEX = Pattern.compile("tiny\t2\t[0-9]\t(([a-zA-Z]*)\t?)+");
	private static final Pattern NAMESPACE_REGEX = Pattern.compile("([a-zA-Z]*)\t?");

	@Override
	public boolean claim(List<String> lines) {
		return lines.size() > 1 && HEADER_REGEX.matcher(lines.get(0)).matches();
	}

	@Override
	public Mapper getMapper(List<String> lines, String from, String to, boolean ignoreErrors) throws MalformedMappingsException {
		Mapper result = new Mapper();
		String currentClass = "";
		String header = HEADER_REGEX.matcher(lines.get(0)).group(1);
		Matcher namespaceMatcher = NAMESPACE_REGEX.matcher(header);

		int namespaceCount = 0;
		int namespaceFrom = -1, namespaceTo = -1;

		while (namespaceMatcher.find()) {
			String ns = namespaceMatcher.group(1);
			if(ns.equals(from)) namespaceFrom = namespaceCount;
			else if(ns.equals(to)) namespaceTo = namespaceCount;
			namespaceCount++;
		}

		if(namespaceFrom == -1 || namespaceTo == -1) {
			throw new MalformedMappingsException(0, "missing requested namespace");
		}

		for(int i = 1; i < lines.size(); i++) {
			String currentLine = lines.get(i);
			String[] tokens = currentLine.trim().split("\t");
			int tabCount = currentLine.indexOf(tokens[0]); // get number of leading tabs
			switch(tabCount) {
				case 0: // classes
					if(tokens.length == 1 + namespaceCount) {
						if(tokens[0].charAt(0) == 'c') {
							currentClass = tokens[1 + namespaceFrom];
							result.getRawMappings().put(currentClass, new ClassData(currentClass, tokens[1 + namespaceTo]));
						} else if(!ignoreErrors)
							throw new MalformedMappingsException(i + 1, "root-level element must be class");
						continue;
					}
					break;
				case 1: // class members
					if(currentClass.isEmpty()) {
						if(ignoreErrors) continue;
						else throw new MalformedMappingsException(i + 1, "class member without parent class");
					}
					switch(tokens[0].charAt(0)) {
						case 'm': // methods
							if(tokens.length == 2 + namespaceCount)
								result.getClassData(currentClass).addMethod(tokens[2 + namespaceFrom], tokens[2 + namespaceTo], tokens[1]);
							else if(!ignoreErrors)
								throw new MalformedMappingsException(i + 1, "incomplete method member");
							continue;
						case 'f': // fields
							if(tokens.length == 2 + namespaceCount)
								result.getClassData(currentClass).addField(tokens[2 + namespaceFrom], tokens[2 + namespaceTo], tokens[1]);
							else if(!ignoreErrors)
								throw new MalformedMappingsException(i + 1, "incomplete field member");
							continue;
					}
					break;
				case 2: // parameters, our mappers don't really support those
					continue;
				default:
					if(tokens[0].charAt(0) == 'c')
						continue; // skip comments
					if(!ignoreErrors)
						throw new MalformedMappingsException(i + 1, "wrong number of tab-separated tokens");
			}
		}
		return result;
	}
}
