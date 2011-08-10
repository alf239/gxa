package uk.ac.ebi.gxa.utils;

import java.io.File;

public class ResourceUtil {
    public static File getClasspathRoot(Class<?> clazz) {
        return new File(clazz.getClassLoader().getResource("").getPath());
    }
}
