import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.verybd.DemoBean;

@Component
public class Spy2 {

    @Autowired
    private DemoBean bean;

    public void spy() throws Exception {
        System.out.println("Hello Kugou!============");
        System.out.println(FieldUtils.readDeclaredField(bean, "map", true));
    }

    public void test01(String arg) {
        System.out.println("arg:" + arg);
    }
}