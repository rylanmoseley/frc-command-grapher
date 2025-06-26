package com.rylanmoseley.commandgrapher;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class DiagramGenerator {
    public static void generate(Path sourceRoot, Path outputPath) throws IOException {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(sourceRoot.toFile())
        );
        ParserConfiguration config = new ParserConfiguration()
            .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17)
            .setSymbolResolver(new JavaSymbolSolver(typeSolver));

        StaticJavaParser.setConfiguration(config);

        List<Path> javaFiles = Files.walk(sourceRoot)
                .filter(p -> p.toString().endsWith(".java"))
                .toList();

        StringBuilder mermaid = new StringBuilder("```mermaid\ngraph TD\n");

        for (Path javaFile : javaFiles) {
            CompilationUnit cu = StaticJavaParser.parse(javaFile);

            cu.findAll(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class).forEach(cls -> {
                String name = cls.getNameAsString();
                if (name.contains("Command")) {
                    mermaid.append("    Command[").append(name).append("]\n");
                } else if (name.contains("Trigger")) {
                    mermaid.append("    Trigger[").append(name).append("]\n");
                    // Additional parsing logic can be added here
                }
            });
        }

        mermaid.append("```\n");
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, mermaid.toString());
    }
}
