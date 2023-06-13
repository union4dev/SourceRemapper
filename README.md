# SourceRemapper
A tool for apply mapping with source code also useful in mcp-pro

this repo is a java version of [RenameSources](https://github.com/MinecraftForge/MCPConfig/blob/master/buildSrc/src/main/groovy/net/minecraftforge/mcpconfig/tasks/RenameSources.groovy) by MinecraftForge (Unofficial)

# Usage
* download [release](https://github.com/union4dev/SourceRemapper/releases)
* `java -jar SourceRemapper.jar [targetFolder] [destinationFolder] [srgFile] [mappingFile]`

## Example
`java -jar SourceRemapper.jar targetSrc/ RemappedSrc/ oldSrg.tsrg client.txt`
