package com.kugou.internal.classloader;

/**
 * 热替换方式加载class文件
 * Created by Alex on 2014/9/20
 */
public class SpyClassLoader extends ClassLoader {

    private byte[] byteCode;
    public SpyClassLoader(byte[] byteCode) {
        this.byteCode = byteCode;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return defineClass(name, this.byteCode, 0, byteCode.length);
    }
}
