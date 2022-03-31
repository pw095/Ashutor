package org.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Query {

    public static String getQuery(String filePath) {

        String queryString = null;

        try {
            queryString = Files.lines(Paths.get(filePath)).collect(Collectors.joining(System.getProperty("line.separator")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return queryString;
    }
}
