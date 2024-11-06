package org.scrollSystem.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EnvLoader {
    public static Map<String, String> loadEnv(String filePath) {
        Map<String, String> envMap = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.filter(line -> !line.startsWith("#") && line.contains("="))
                    .forEach(line -> {
                        String[] keyValue = line.split("=", 2);
                        envMap.put(keyValue[0], keyValue[1]);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return envMap;
    }
}

