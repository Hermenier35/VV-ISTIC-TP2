package fr.istic.vv;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CyclomaticComplexity {

    public static void main(String[] args) {
        String projectPath = "C:\\Users\\asmaa\\Documents\\Master2\\VV\\test.java";
        List<MethodComplexity> methodComplexities = new ArrayList<>();
        JavaParser javaParser = new JavaParser();

        try {
            Files.walk(Paths.get(projectPath))
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".java"))
                    .forEach(file -> {
                        try {
                            System.out.println("Analyse du fichier : " + file.toString());
                            FileInputStream in = new FileInputStream(file.toFile());
                            CompilationUnit cu = javaParser.parse(
                                    new InputStreamReader(in, StandardCharsets.UTF_8)
                            ).getResult().orElseThrow(() -> new RuntimeException("Erreur de parsing"));

                            String packageName = cu.getPackageDeclaration()
                                    .map(pd -> pd.getNameAsString())
                                    .orElse("default");

                            cu.accept(new VoidVisitorAdapter<Void>() {
                                private String currentClassName = "";

                                @Override
                                public void visit(ClassOrInterfaceDeclaration cid, Void arg) {
                                    this.currentClassName = cid.getNameAsString();
                                    super.visit(cid, arg);
                                }

                                @Override
                                public void visit(MethodDeclaration md, Void arg) {
                                    super.visit(md, arg);
                                    int[] ccAndNodesEdges = calculateCyclomaticComplexity(md);
                                    int cc = ccAndNodesEdges[0];
                                    int numNodes = ccAndNodesEdges[1];
                                    int numEdges = ccAndNodesEdges[2];
                                    methodComplexities.add(new MethodComplexity(
                                            currentClassName,
                                            packageName,
                                            md.getSignature().asString(),
                                            cc,
                                            numNodes,
                                            numEdges
                                    ));
                                }
                            }, null);

                        } catch (Exception e) {
                            System.err.println("Erreur lors de l'analyse du fichier : " + file);
                            e.printStackTrace();
                        }
                    });

            generateReport(methodComplexities);
        } catch (Exception e) {
            System.err.println("Erreur lors du parcours des fichiers dans le projet.");
            e.printStackTrace();
        }
    }

   /* public static int[] calculateCyclomaticComplexity(MethodDeclaration method) {
        int numNodes = 2;  // Starting with the method itself (entry point)
        int numEdges = 1;  // Edges start at 0
        int numConditions = 0; // Counting conditions (if, while, for, etc.)

        if (method.getBody().isPresent()) {
            BlockStmt body = method.getBody().get();
            numEdges += body.findAll(com.github.javaparser.ast.stmt.IfStmt.class).size() * 2;
            numConditions += body.findAll(com.github.javaparser.ast.stmt.Statement.class).size();
            numConditions -= body.findAll(com.github.javaparser.ast.stmt.ExpressionStmt.class).size();
            numEdges += numConditions - body.findAll(com.github.javaparser.ast.stmt.IfStmt.class).size();
            numNodes += numConditions;
            // Each condition adds 2 edges: one for the true path and one for the false path
          //  numEdges += numStatements * 2;  // Two paths for each condition (true/false)
        }

        // Formula: E - N + 2P, where P = 1 for a single method (method is considered as one connected component)
        int cyclomaticComplexity = numEdges - numNodes + 2 ;  // Complexity = edges - nodes + 2
        return new int[] {cyclomaticComplexity, numNodes, numEdges};  // Return complexity, nodes, and edges


    }*/
   public static int[] calculateCyclomaticComplexity(MethodDeclaration method) {
       int numNodes = 2; // Start node and End node
       int numEdges = 0; // Edges in the control flow graph
       int numConditions = 0; // Number of conditions (decision points)

       if (method.getBody().isPresent()) {
           BlockStmt body = method.getBody().get();

           // Count if statements
           int ifStatements = body.findAll(com.github.javaparser.ast.stmt.IfStmt.class).size();
           int whileStatements = body.findAll(com.github.javaparser.ast.stmt.WhileStmt.class).size();
           int forStatements = body.findAll(com.github.javaparser.ast.stmt.ForStmt.class).size();
           int forEachStatements = body.findAll(com.github.javaparser.ast.stmt.ForEachStmt.class).size();
           int switchStatements = body.findAll(com.github.javaparser.ast.stmt.SwitchStmt.class).size();
           int catchClauses = body.findAll(com.github.javaparser.ast.stmt.CatchClause.class).size();

           // Count total conditions
           numConditions = ifStatements + whileStatements + forStatements + forEachStatements + catchClauses;

           // Add 2 edges for each condition (true and false paths)
           numEdges += numConditions * 2;

           // Handle switch statements separately
           for (SwitchStmt switchStmt : body.findAll(com.github.javaparser.ast.stmt.SwitchStmt.class)) {
               int caseCount = switchStmt.getEntries().size();
               numEdges += caseCount; // Each "case" or "default" is a branch
               numConditions += caseCount - 1; // Only count case conditions, not default
           }

           // Add nodes for conditions
           numNodes += numConditions;

           // Add nodes for sequential statements (basic blocks)
           int basicBlocks = body.findAll(com.github.javaparser.ast.stmt.BlockStmt.class).size();
           numNodes += basicBlocks;

           // Add edges for sequential execution
           numEdges += basicBlocks - 1; // Each block connects to the next
       }

       // Compute Cyclomatic Complexity using McCabe's formula
       int cyclomaticComplexity = numEdges - numNodes + 2;

       return new int[]{cyclomaticComplexity, numNodes, numEdges};
   }



    public static void generateReport(List<MethodComplexity> methodComplexities) {
        methodComplexities.forEach(method -> {
            System.out.println("Package: " + method.packageName);
            System.out.println("Class: " + method.className);
            System.out.println("Method: " + method.methodName);
            System.out.println("Cyclomatic Complexity: " + method.complexity);
            System.out.println("numNodes: " + method.numNodes);
            System.out.println("numEdges: " + method.numEdges);
            System.out.println("--------");
        });
    }

    public static class MethodComplexity {
        String className;
        String packageName;
        String methodName;
        int complexity;
        int numNodes;
        int numEdges;

        MethodComplexity(String className, String packageName, String methodName, int complexity, int numNodes, int numEdges) {
            this.packageName = packageName;
            this.className = className;
            this.methodName = methodName;
            this.complexity = complexity;
            this.numNodes = numNodes;
            this.numEdges = numEdges;
        }
    }
}
