package org.example.project;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectContext {

    public final static ConcurrentHashMap<String, FileNode> openFiles = new ConcurrentHashMap<>();

    public static void didOpenFile(String url, String textContent){
      FileNode fileNode = new FileNode(textContent,url);
      fileNode.processNode();
      openFiles.put(url, fileNode);
    }
    public static FileNode getFileNode(String url){
        return openFiles.get(url);
    }
}
