package com.kugou.internal.compiler;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by Alex on 2014/9/20
 */
public class CompilerHelper {

    public static File getTargetFile() {
        return FileUtils.getTempDirectory();
    }
}
