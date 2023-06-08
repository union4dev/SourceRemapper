package org.union4dev.sourceremapper;

import net.minecraftforge.srgutils.IMappingFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SourceRemapper {

    private final File target;
    private final File destination;
    private final File srgFile;
    private final File mappingFile;

    public SourceRemapper(File target, File destination, File srgFile, File mappingFile) {
        this.target = target;
        this.destination = destination;
        this.srgFile = srgFile;
        this.mappingFile = mappingFile;
    }

    public void run() throws IOException {
        final Map<String, String> map = loadMappings();
        final Path root = target.toPath();

        try (Stream<Path> stream = Files.walk(root)) {
            stream.forEach(entry -> {
                final File file = entry.toFile();
                if (!file.isDirectory()) {
                    final String path = root.relativize(entry).toString();
                    final File out = new File(destination, path);

                    try {
                        if (!out.getParentFile().exists())
                            if (!out.getParentFile().mkdirs()) {
                                System.out.println("Failed to create folder " + path);
                                return;
                            }
                        if (!out.exists())
                            if (!out.createNewFile()) {
                                System.out.println("Failed to create file " + out.getName() + " in " + path);
                                return;
                            }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String data;
                    if (path.endsWith(".java")) {
                        try {
                            final String content = new String(Files.readAllBytes(entry));

                            final Pattern pattern = Pattern.compile("f_\\d+_|m_\\d+_|func_\\d+_[a-zA-Z_]+|field_\\d+_[a-zA-Z_]+");
                            final Matcher matcher = pattern.matcher(content);

                            final StringBuilder stringBuilder = new StringBuilder();
                            while (matcher.find()) {
                                String name = matcher.group();
                                matcher.appendReplacement(stringBuilder, map.getOrDefault(name, name));
                            }
                            matcher.appendTail(stringBuilder);
                            data = stringBuilder.toString();
                        } catch (IOException e) {
                            data = "";
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            data = new String(Files.readAllBytes(entry));
                        } catch (IOException e) {
                            data = "";
                            e.printStackTrace();
                        }
                    }

                    try {
                        Files.write(out.toPath(), data.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private Map<String, String> loadMappings() throws IOException {
        final Map<String, String> map = new LinkedHashMap<>();
        final IMappingFile srg = IMappingFile.load(srgFile);
        final IMappingFile mapping = IMappingFile.load(mappingFile).reverse();
        srg.getClasses().forEach(scls -> {
            final IMappingFile.IClass ocls = mapping.getClass(scls.getOriginal());
            if (ocls != null) {
                scls.getFields().forEach(sfld -> {
                    if (sfld.getMapped().startsWith("f_") || sfld.getMapped().startsWith("field_"))
                        map.put(sfld.getMapped(), ocls.remapField(sfld.getOriginal()));
                });
                scls.getMethods().forEach(smtd -> {
                    if (smtd.getMapped().startsWith("m_") || smtd.getMapped().startsWith("func_"))
                        map.put(smtd.getMapped(), ocls.remapMethod(smtd.getOriginal(), smtd.getDescriptor()));
                });
            }
        });
        return map;
    }
}
