# Lillero-mapper
A tiny library to process mappings in various formats.

## Why?
I initially [meant to fork Enigma](https://github.com/FabricMC/Enigma), but honestly it was far more than I needed, even when stripped down to its core library.

The purpose of this library is to act as base for [Lillero-processor](https://github.com/zaaarf/lillero-processor/), to work with various mapping formats.

## Usage
Simply call `MapperProvider.getMapper()` and pass it a `List` of `String`s representing the mapping file's contents, line by line. Call then `populate()` on the resulting `IMapper`, passing the same `List` once again, and you'll have an object that can process stuff back and forth - granted that a mapper capable of parsing the input was found.

## Supported formats
These are the formats currently supported by this library:
- `srg`
- `tsrg`
- `tinyv2`
- `multi` (see below)

### Multimapper
A case where multiple passes of deobfuscation may be needed is also supported, although unrecommended; I would also very much advise against referencing an untrusted multimapper in a remote location, for reasons that will be obvious to you in a second. A multimapper looks like this:

```
lll multimapper
<url-or-path-for-first-mapping>
<url-or-path-for-second-mapping>
...
<url-or-path-for-last-mapping>
```

You obviously don't want a remote location you don't control telling you what to download on your computer.

The passes are applied in the provided order, from the first to the last.
