package org.example.project;

import org.example.communication.DTO.Range;

import java.util.Objects;

public class FunctionDeclaration {

    public enum Kind {
        BUILTIN,
        USER,
        SPECIAL_FORM
    }
    public final String name;
    public final Kind kind;
    public final int arity;
    public Range range;
    public String description;


    public FunctionDeclaration(String name, int arity, Kind kind) {
        this.name = name;
        this.arity = arity;
        this.kind = kind;
    }

    public FunctionDeclaration(String name, int arity,Kind kind, Range range) {
        this.name = name;
        this.arity = arity;
        this.kind = kind;
        this.range = range;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FunctionDeclaration that = (FunctionDeclaration) o;
        return arity == that.arity && Objects.equals(name, that.name) && kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, kind, arity);
    }

    @Override
    public String toString() {
        return "FunctionDeclaration{" +
                "name='" + name + '\'' +
                ", kind=" + kind +
                ", arity=" + arity +
                ", range=" + range +
                '}';
    }
}
