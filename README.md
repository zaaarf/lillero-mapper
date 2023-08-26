# Lillero-mapper
A tiny library to process mappings in various formats.

## Why?
I initially [meant to fork Enigma](https://github.com/zaaarf/enigma), but honestly it was far more than I needed, even when stripped down to its core library.

The purpose of this library is to act as base for the [Lillero processor](https://github.com/zaaarf/lillero-processor/), to work with various mapping formats.

## Usage
Simply call `MapperProvider.getMapper()` and pass it a `List` of `String`s representing the mapping file's contents, line by line. Call then `populate()` on the resulting `IMapper`, passing the same `List` once again, and you'll have an object that can process stuff back and forth - granted that a mapper capable of parsing the input was found.
