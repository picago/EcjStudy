package tk.verybd;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 2014/9/20
 */
@Component
public class DemoBean {

    private Map<String, String> map = new HashMap<>();

    @PostConstruct
    public void init() {
        map.put("a", "b");
    }
}
