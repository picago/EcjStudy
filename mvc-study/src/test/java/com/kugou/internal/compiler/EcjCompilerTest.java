package com.kugou.internal.compiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class EcjCompilerTest {

    private ApplicationContext applicationContext;
    @Before
    public void setUp() {
        applicationContext = new ClassPathXmlApplicationContext("application-logic.xml");
    }

    @Test
    public void testCompileOne() throws Exception {
        String code = FileUtils.readFileToString(new File("C:\\app\\lab\\study\\EcjStudy\\mvc-study\\src\\main\\resources\\Spy.java"));
        Class clazz = EcjCompiler.compileOne(code);
        MethodUtils.invokeMethod(clazz.newInstance(), "spy");
    }
    @Test
    public void testInSpring() throws Exception {

        String code = FileUtils.readFileToString(new File("C:\\app\\lab\\study\\EcjStudy\\mvc-study\\src\\main\\resources\\Spy.java"));
        Object target = applicationContext.getBean(com.kugou.internal.SpyHelper.class).codeToObject(code);
        System.out.println(FieldUtils.readDeclaredField(target, "bean", true));
        MethodUtils.invokeMethod(target, "spy");
        System.out.println(FieldUtils.readDeclaredField(target, "sb", true));
    }

    /**
     * 测试gc 类卸载， 由于每次都使用热加载， 需要测试一下类是否被卸载了
     * 标准输出若包含Unloading class，那就是在卸载类了， 需要开启如下这些参数
     * -XX:MaxPermSize=1m
     * -verbose
     * -XX:+TraceClassLoading
     * @throws Exception
     */
    @Test
    public void testGcUnloading() throws Exception {

        String code = FileUtils.readFileToString(new File("C:\\app\\lab\\study\\EcjStudy\\mvc-study\\src\\main\\resources\\Spy.java"));
        while (true) {
            applicationContext.getBean(com.kugou.internal.SpyHelper.class).codeToObject(code);
        }
    }
}