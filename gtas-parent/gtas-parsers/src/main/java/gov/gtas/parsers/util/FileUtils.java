/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

public final class FileUtils {
    private FileUtils() {
    }

    public static byte[] readSmallFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public static void writeToFile(String fileName, String s) throws IOException, FileNotFoundException {
        FileOutputStream output = null;
        try {
            byte[] bytes = s.getBytes();
            output = new FileOutputStream(new File(fileName));
            IOUtils.write(bytes, output);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
}
