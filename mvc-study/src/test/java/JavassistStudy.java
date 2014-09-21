import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

/**
 * Created by Alex on 2014/9/20
 */
public class JavassistStudy {

    @Test
    public void study() throws Exception {
        Spy2 sy = new Spy2();
        ClassPool pool = ClassPool.getDefault();
        Loader cl = new Loader(pool);
        CtClass cc = pool.get("Spy2");
        cc.setName("Spy");
        Class c = cl.loadClass("Spy");
        cc.defrost();
        cc.setName("Spy1");
        Class c1 = cl.loadClass("Spy1");
        cc.detach();
        System.out.println(c);
        System.out.println(c1);
        System.out.println(c==c1);
    }

    @Test
    public void testMethod() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("Spy2");
        CtMethod method = cc.getDeclaredMethod("test01");
        method.insertBefore("System.out.println($1);");
        Class c = cc.toClass();
        Spy2 sp = (Spy2) c.newInstance();
        sp.test01("Hello");
    }
    @Test
    public void testModifyMethod() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("Spy2");

        cc.addField(CtField.make("final StringBuilder sb = new StringBuilder(\"<<\");", cc));
        CtMethod method = cc.getDeclaredMethod("test01");
        method.instrument(new ExprEditor() {

            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().endsWith("PrintStream")
                        && m.getMethodName().equals("println")) {
                    m.replace("if($0==System.out) sb.append($1+\"<br/>\"); else  $proceed($$);");
                }
            }
        });
        Class c = cc.toClass();
        Spy2 sp = (Spy2) c.newInstance();
        sp.test01("Hello");
        StringBuilder sb = (StringBuilder) FieldUtils.readDeclaredField(sp, "sb", true);
        System.out.println(sb.toString());
    }
}
