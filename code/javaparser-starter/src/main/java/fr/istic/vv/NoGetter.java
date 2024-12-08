package fr.istic.vv;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class NoGetter {

    public static void main(String[] args) throws IOException {
        String path;

        if (args.length < 1) {
            System.out.println("Veuillez spécifier le chemin vers le code source Java :");
            Scanner scanner = new Scanner(System.in);
            path = scanner.nextLine();
        } else {
            path = args[0];
        }

        File projectDirectory = new File(path);
        if (!projectDirectory.isDirectory()) {
            System.out.println("Le chemin spécifié n'est pas un répertoire valide.");
            return;
        }

        SourceRoot sourceRoot = new SourceRoot(projectDirectory.toPath());

        try {
            sourceRoot.tryToParse("");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'analyse des fichiers : " + e.getMessage());
            return;
        }

        generateReport(sourceRoot);
    }

    public static void generateReport(SourceRoot sourceRoot) throws IOException {
        StringBuilder report = new StringBuilder();
        report.append("| Nom du champ | Classe | Package |\n");
        report.append("|--------------|--------|---------|\n");

        sourceRoot.getCompilationUnits().forEach(compilationUnit -> {
            compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classDeclaration -> {
                if (classDeclaration.isPublic()) {
                    classDeclaration.findAll(FieldDeclaration.class).forEach(field -> {
                        if (field.isPrivate()) {
                            field.getVariables().forEach(variable -> {
                                boolean hasGetter = classDeclaration.getMethods().stream()
                                        .anyMatch(method -> isGetterMethod(method, variable.getNameAsString()));

                                if (!hasGetter) {
                                    report.append("| ").append(variable.getNameAsString())
                                            .append(" | ").append(classDeclaration.getNameAsString())
                                            .append(" | ")
                                            .append(classDeclaration.getFullyQualifiedName().orElse("N/A"))
                                            .append(" |\n");
                                }
                            });
                        }
                    });
                }
            });
        });

        Files.write(Paths.get("rapport.md"), report.toString().getBytes());
        System.out.println("Rapport généré : rapport.md");
    }

    public static boolean isGetterMethod(MethodDeclaration method, String fieldName) {
        String methodName = method.getNameAsString();
        return method.isPublic()
                && methodName.equalsIgnoreCase("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1))
                && method.getParameters().isEmpty();
    }
}
