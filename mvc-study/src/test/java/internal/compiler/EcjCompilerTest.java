package internal.compiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;

import java.io.File;

public class EcjCompilerTest {

    @Test
    public void testCompileOne() throws Exception {
        String code = FileUtils.readFileToString(new File("C:\\app\\lab\\study\\EcjStudy\\mvc-study\\src\\main\\resources\\Spy.java"));
        Class clazz = EcjCompiler.compileOne(code);
//        MethodUtils.invokeExactStaticMethod(clazz, "main");
        MethodUtils.invokeMethod(clazz.newInstance(), "spy");
    }
}