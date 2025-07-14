package ftbsc.lll.mapper.impl;

import com.google.auto.service.AutoService;
import ftbsc.lll.exceptions.MalformedMappingsException;
import ftbsc.lll.mapper.IMappingFormat;
import ftbsc.lll.mapper.data.FieldData;
import ftbsc.lll.mapper.data.MethodData;
import ftbsc.lll.mapper.data.MethodSignature;
import ftbsc.lll.mapper.utils.Mapper;
import ftbsc.lll.mapper.data.ClassData;
import ftbsc.lll.mapper.utils.MappingUtils;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link IMappingFormat} capable of parsing TinyV2 mappings.
 */
@AutoService(IMappingFormat.class)
public class TinyV2Mapper implements IMappingFormat {
	private static final Pattern HEADER_REGEX = Pattern.compile("tiny\t2\t[0-9]\t((([a-zA-Z]+)\t?)+)");
	private static final Pattern NAMESPACE_REGEX = Pattern.compile("([a-zA-Z]+)\t?");

	@Override
	public boolean claim(List<String> lines) {
		return lines.size() > 1 && HEADER_REGEX.matcher(lines.get(0)).matches();
	}

	@Override
	public Mapper getMapper(List<String> lines, String from, String to, boolean ignoreErrors) throws MalformedMappingsException {
		Matcher headerMatcher = HEADER_REGEX.matcher(lines.get(0));
		headerMatcher.find();
		String header = headerMatcher.group(1);

		Matcher namespaceMatcher = NAMESPACE_REGEX.matcher(header);
		int namespaceCount = 0;
		int namespaceFrom = -1, namespaceTo = -1;

		while(namespaceMatcher.find()) {
			String ns = namespaceMatcher.group(1).trim();
			if(ns.equals(from)) namespaceFrom = namespaceCount;
			else if(ns.equals(to)) namespaceTo = namespaceCount;
			namespaceCount++;
		}

		if(namespaceCount < 2) {
			throw new MalformedMappingsException(0, "not enough namespaces");
		}

		if(namespaceFrom == -1 || namespaceTo == -1) {
			if(namespaceCount == 2 && from == null && to == null) { // fallback for legacy compatibility
				namespaceFrom = 0;
				namespaceTo = 1;
			} else {
				throw new MalformedMappingsException(0, "missing requested namespace");
			}
		}

		Mapper mapper = new Mapper();
		populateMapper(mapper, lines, namespaceCount, namespaceFrom, namespaceTo, ignoreErrors);

		// we need a second "bridge" mapper that will act as a (temporary) bridge between ns #1
		// (which is used to express descriptors) and whatever "from" is
		Mapper bridge = new Mapper();
		populateMapper(bridge, lines, namespaceCount, 0, namespaceFrom, ignoreErrors);

		Mapper result = new Mapper();
		for(String nameMapped : mapper.getRawMappings().keySet()) {
			ClassData mapperData = mapper.getClassData(nameMapped);
			ClassData resultData = new ClassData(mapperData.name, mapperData.nameMapped);

			for(MethodSignature signature : mapperData.getMethods().keySet()) {
				MethodData md = mapperData.getMethods().get(signature);
				resultData.addMethod(
					md.signature.name,
					md.nameMapped,
					MappingUtils.mapMethodDescriptor(md.signature.descriptor, bridge, false)
				);
			}

			for(String field : mapperData.getFields().keySet()) {
				FieldData fd = mapperData.mapField(field);
				if(fd.descriptor != null) {
					Type type = MappingUtils.mapType(Type.getType(fd.descriptor), bridge, false);
					if(type.getSort() >= Type.ARRAY) {
						resultData.addField(fd.name, fd.nameMapped, type.getInternalName());
						continue;
					}
				}

				resultData.addField(fd.name, fd.nameMapped);
			}

			result.getRawMappings().put(resultData.name, resultData);
		}

		return result;
	}

	private static void populateMapper(
		Mapper mapper,
		List<String> lines,
		int namespaceCount,
		int namespaceFrom,
		int namespaceTo,
		boolean ignoreErrors
	) {
		String currentClass = "";
		for(int i = 1; i < lines.size(); i++) {
			String currentLine = lines.get(i);
			String[] tokens = currentLine.trim().split("\t");
			int tabCount = currentLine.indexOf(tokens[0]); // get number of leading tabs
			switch(tabCount) {
				case 0: // classes
					if(tokens.length == 1 + namespaceCount) {
						if(tokens[0].charAt(0) == 'c') {
							currentClass = tokens[1 + namespaceFrom];
							mapper.getRawMappings().put(currentClass, new ClassData(currentClass, tokens[1 + namespaceTo]));
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
							if(tokens.length == 2 + namespaceCount) {
								mapper.getClassData(currentClass).addMethod(tokens[2 + namespaceFrom], tokens[2 + namespaceTo], tokens[1]);
							} else if(!ignoreErrors) throw new MalformedMappingsException(i + 1, "incomplete method member");
							continue;
						case 'f': // fields
							if(tokens.length == 2 + namespaceCount) {
								mapper.getClassData(currentClass).addField(tokens[2 + namespaceFrom], tokens[2 + namespaceTo], tokens[1]);
							} else if(!ignoreErrors) throw new MalformedMappingsException(i + 1, "incomplete field member");
							continue;
					}
					break;
				case 2: // parameters, our mappers don't really support those
					continue;
				default:
					if(tokens[0].charAt(0) == 'c') {
						continue; // skip comments
					}

					if(!ignoreErrors) throw new MalformedMappingsException(i + 1, "wrong number of tab-separated tokens");
			}
		}
	}
}
