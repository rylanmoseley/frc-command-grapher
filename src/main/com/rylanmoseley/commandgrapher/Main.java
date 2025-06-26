package com.rylanmoseley.commandgrapher;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: <source-path> <output-path>");
            System.exit(1);
        }
        Path source = Path.of(args[0]);
        Path output = Path.of(args[1]);
        System.out.println("Hello from Command Grapher!");
        DiagramGenerator.generate(source, output);
    }
}
