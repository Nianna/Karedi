package com.github.nianna.karedi.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResourceUtils {

    private ResourceUtils() {

    }

    public static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        try (Scanner scan = new Scanner(ResourceUtils.class.getResourceAsStream(path), StandardCharsets.UTF_8)) {
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine());
            }
        }
        return lines;
    }
}
