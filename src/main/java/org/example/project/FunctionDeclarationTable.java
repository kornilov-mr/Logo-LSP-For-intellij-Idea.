package org.example.project;

import java.util.HashMap;
import java.util.Map;

public class FunctionDeclarationTable {

    public final Map<String, FunctionDeclaration> declarationsMap = new HashMap<>();

    public FunctionDeclaration getFunctionDeclaration(String name){
        if(StandardFunctionDeclaration.getDeclaration().contains(name))
            return StandardFunctionDeclaration.getDeclaration().getFunctionDeclaration(name);
        return declarationsMap.get(name.toLowerCase());
    }
    public boolean contains(String name){
        return declarationsMap.containsKey(name.toLowerCase()) || StandardFunctionDeclaration.getDeclaration().contains(name.toLowerCase());
    }
    public void add(String name, FunctionDeclaration declaration){
        declarationsMap.put(name.toLowerCase(), declaration);
    }
    public void clearUserFunctions(){
        declarationsMap.clear();
    }

}
