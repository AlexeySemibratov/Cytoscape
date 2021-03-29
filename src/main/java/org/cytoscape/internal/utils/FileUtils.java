package org.cytoscape.internal.utils;

import javax.swing.*;
import java.io.File;

public class FileUtils {

    public static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf('.') > 0) {
            return fileName.substring(fileName.lastIndexOf('.') + 1);
        }
        return null;
    }

    public static boolean isFileExtension(String ext, String... exts) {
        if (ext == null) return false;
        for (String e : exts) {
            if (ext.equals(e)) return true;
        }
        return false;
    }

    public static boolean isFileExtension(File file, String... exts) {
        if (file == null) return false;
        return isFileExtension(getFileExtension(file.getName()), exts);
    }

    public static String getSessionSimpleName(String fullName) {
        char[] chars = fullName.toCharArray();
        StringBuilder result = new StringBuilder();
        int beginIndex = chars.length - 1;
        for (; beginIndex >= 0; beginIndex--) {
            if (chars[beginIndex] == '\\') {
                beginIndex++;
                break;
            }
        }

        for (; beginIndex < chars.length; beginIndex++) {
            result.append(chars[beginIndex]);
        }

        return result.toString();
    }
}
