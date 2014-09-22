package com.kugou.internal;

import com.kugou.internal.compiler.EcjCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;


/**
 * Created by Alex on 2014/9/20
 */
@Component
public class SpyHelper {

    @Autowired
    private ApplicationContext applicationContext;
    /**
     * java代码转成java的类实例
     * @param javaCode java代码字符串
     * @return 由java代码生成的类实例
     * @throws Exception
     */
    public Object codeToObject(String javaCode) throws Exception {
        Class clazz = EcjCompiler.compileOne(javaCode);
        Object instance = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (isInjectAnnotationPresent(field)) {
                Object o = findBean(field);
                field.setAccessible(true);
                field.set(instance, o);
            }
        }
        return instance;
    }

    private Object findBean(Field field) {
        if (field.isAnnotationPresent(Resource.class)) {
            Resource resource = field.getAnnotation(Resource.class);
            return applicationContext.getBean(resource.name());
        } else if (field.isAnnotationPresent(Qualifier.class)) {
            Qualifier qualifier = field.getAnnotation(Qualifier.class);
            return applicationContext.getBean(qualifier.value());
        } else if (field.isAnnotationPresent(Autowired.class)) {
            return applicationContext.getBean(field.getType());
        }
        throw new RuntimeException("依赖注入失败: " + field.getName());
    }

    /**
     * 依赖注入的那些注解是否存在， 也就是@Autowired, @Resource
     * @param field
     * @return
     */
    private boolean isInjectAnnotationPresent(Field field) {
        return field.isAnnotationPresent(Autowired.class)
                || field.isAnnotationPresent(Resource.class);
    }

}
