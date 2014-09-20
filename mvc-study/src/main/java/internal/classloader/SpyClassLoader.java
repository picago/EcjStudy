package internal.classloader;

import internal.compiler.CompilerHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alex on 2014/9/20
 */
public class SpyClassLoader extends ClassLoader {



    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        File file = new File(CompilerHelper.getTargetFile(), "Spy.class");
        byte[] byteCode = new byte[0];
        try {
            byteCode = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defineClass(name, byteCode, 0, byteCode.length);
    }
}
