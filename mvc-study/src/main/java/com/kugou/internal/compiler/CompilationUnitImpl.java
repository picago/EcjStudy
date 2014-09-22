package com.kugou.internal.compiler;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alex on 2014/9/20
 */
public class CompilationUnitImpl implements ICompilationUnit {

    private File file;

    public CompilationUnitImpl(File file) {
        this.file = file;
    }

    @Override
    public char[] getContents() {
        try {
            return FileUtils.readFileToString(file).toCharArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public char[] getMainTypeName() {
        return file.getName().replace(".java", "").toCharArray();
    }

    @Override
    public char[][] getPackageName() {
        return new char[][]{};
    }

    public boolean ignoreOptionalProblems() {
        return false;
    }

    @Override
    public char[] getFileName() {
        return this.file.getName().toCharArray();
    }
}
