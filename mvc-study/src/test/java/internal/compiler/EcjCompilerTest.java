package internal.compiler;

import internal.SpyHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;

import java.io.File;

public class EcjCompilerTest {

    @Test
    public void testCompileOne() throws Exception {
        String code = FileUtils.readFileToString(new File("C:\\app\\lab\\study\\EcjStudy\\mvc-study\\src\\main\\resources\\Spy.java"));
        Class clazz = EcjCompiler.compileOne(code);
        MethodUtils.invokeMethod(clazz.newInstance(), "spy");
    }
    @Test
    public void testInSpring() throws Exception {
        String code = FileUtils.readFileToString(new File("C:\\app\\lab\\study\\EcjStudy\\mvc-study\\src\\main\\resources\\Spy.java"));
        Object target = SpyHelper.codeToObject(code);
        System.out.println(FieldUtils.readDeclaredField(target, "bean", true));
    }
}