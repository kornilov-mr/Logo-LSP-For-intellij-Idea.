package ast;

import org.example.project.FileNode;
import org.example.project.ast.ConvertToAST;
import org.example.project.ast.ProgramNode;
import org.example.project.staticAnalyser.ScopeBuilder;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ParserAbstractTest {
    private boolean writeExpected = false;

    public ParserAbstractTest(boolean writeExpected) {
        this.writeExpected = writeExpected;
    }

    public File resolveFileFromLogoToTxt(File logoFile) {
        String path = logoFile.getAbsolutePath();
        String pathToExpected = path.replace("programs", "expected")
                .replace(".logo", ".txt");
        return new File(pathToExpected);
    }

    public ProgramNode parseFileNodeAndGetScopes(FileNode fileNode,String fileUrl, boolean writeExpected) {
        ProgramNode nodes = ConvertToAST.convert(fileNode);
        if (writeExpected) {
            File expected = resolveFileFromLogoToTxt(new File(fileUrl));
            try {
                expected.delete();
                Files.createFile(expected.toPath());
                Files.writeString(expected.toPath(), ScopeBuilder.PrintAllScopesInProgram(nodes));
            } catch (IOException e) {
                System.out.println("Failed to create or write to a expected fil");
                throw new RuntimeException(e);
            }
        }
        return nodes;
    }

    public ProgramNode parseFileNode(FileNode fileNode,String fileUrl, boolean writeExpected) {
        ProgramNode nodes = ConvertToAST.convert(fileNode);
        if (writeExpected) {
            File expected = resolveFileFromLogoToTxt(new File(fileUrl));
            try {
                expected.delete();
                Files.createFile(expected.toPath());
                Files.writeString(expected.toPath(), nodes.toString());
            } catch (IOException e) {
                System.out.println("Failed to create or write to a expected fil");
                throw new RuntimeException(e);
            }
        }
        return nodes;
    }

    protected void assertMatchesExpectedScopes(File logoFile) throws IOException {
        String logo = Files.readString(logoFile.toPath().toAbsolutePath());
        String expected = Files.readString(resolveFileFromLogoToTxt(logoFile).toPath());

        ProgramNode programNode = parseFileNodeAndGetScopes(new FileNode(logo), logoFile.getAbsolutePath(), writeExpected);

        Assertions.assertEquals(expected, ScopeBuilder.PrintAllScopesInProgram(programNode));

    }

    protected void assertMatchesExpectedParsing(File logoFile) throws IOException {
        String logo = Files.readString(logoFile.toPath().toAbsolutePath());
        String expected = Files.readString(resolveFileFromLogoToTxt(logoFile).toPath());

        ProgramNode programNode = parseFileNode(new FileNode(logo),logoFile.getAbsolutePath(), writeExpected);

        Assertions.assertEquals(expected, programNode.toString());
    }
}
