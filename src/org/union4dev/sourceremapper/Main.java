package org.union4dev.sourceremapper;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java -jar SourceRemapper.jar [targetFolder] [destinationFolder] [srgFile] [mappingFile]");
            return;
        }

        try {
            new SourceRemapper(new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3])).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
