package org.example.project;

import java.util.HashMap;
import java.util.Map;

public class ProjectContext {

    public static final FunctionDeclarationTable standardFunctions = new FunctionDeclarationTable();
    public final static Map<String, FileNode> openFiles = new HashMap<>();

    public static void didOpenFile(String url, String textContent){
      FileNode fileNode = new FileNode(textContent,url);
      fileNode.processNode();
      openFiles.put(url, fileNode);
    }
    public static FileNode getFileNode(String url){
        return openFiles.get(url);
    }
}
