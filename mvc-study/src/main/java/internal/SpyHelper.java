package internal;

import internal.compiler.EcjCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.lang.reflect.Field;

/**
 * Created by Alex on 2014/9/20
 */
public class SpyHelper {
    /**
     * java代码转成java的类实例
     * @param javaCode java代码字符串
     * @return 由java代码生成的类实例
     * @throws Exception
     */
    public static Object codeToObject(String javaCode) throws Exception {
        Class clazz = EcjCompiler.compileOne(javaCode);
        Object instance = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        ApplicationContext applicationContext = getSpringContext();
        for (Field field : fields) {
            if (isInjectAnnotationPresent(field)) {
                Object o = findBean(applicationContext, field);
                field.setAccessible(true);
                field.set(instance, o);
            }
        }
        return instance;
    }

    private static Object findBean(ApplicationContext context, Field field) {
        if (field.isAnnotationPresent(Resource.class)) {
            Resource resource = field.getAnnotation(Resource.class);
            return context.getBean(resource.name());
        } else if (field.isAnnotationPresent(Qualifier.class)) {
            Qualifier qualifier = field.getAnnotation(Qualifier.class);
            return context.getBean(qualifier.value());
        } else if (field.isAnnotationPresent(Autowired.class)) {
            return context.getBean(field.getType());
        }
        throw new RuntimeException("依赖注入失败: " + field.getName());
    }

    /**
     * 依赖注入的那些注解是否存在， 也就是@Autowired, @Resource
     * @param field
     * @return
     */
    private static boolean isInjectAnnotationPresent(Field field) {
        return field.isAnnotationPresent(Autowired.class)
                || field.isAnnotationPresent(Resource.class);
    }

    public static ApplicationContext getSpringContext() {
        return new ClassPathXmlApplicationContext("application-logic.xml");
    }
}
