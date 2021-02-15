package prosayj.framework.common.utils.easycaptchautil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author yangjian201127@credithc.com
 * @date 2020-12-02 下午 02:38
 * @since 1.0.0
 */
public class TestFilterList {
    public static final String K0_1 = "k0jghs";
    public static final String K0_2 = "k0jg";
    public static final String K0_3 = "k0sl";

    public static void main(String[] args) {
        List<HashMap<String, String>> before = new ArrayList<HashMap<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("k0jghs", "1111");
                put("k0jg", "1111");
            }});
            add(new HashMap<String, String>() {{
                put("k0jghs", "2222");
                put("k0sl", "2222");
            }});
            add(new HashMap<String, String>() {{
                put("k0jghs", "4444");
                put("k0jg", null);
                put("k0sl", "4444");
            }});
            add(new HashMap<String, String>() {{
                put("k0jghs", "4444");
                put("k0jg", "4444");
                put("k0sl", "4444");
            }});
        }};

        System.out.println("过滤之前：" + before);
        List<HashMap<String, String>> after = before.stream()
                .filter(mapData -> (mapData.get(K0_1) != null) && (mapData.get(K0_2) != null) && (mapData.get(K0_3) != null))
                .collect(Collectors.toList());
        System.out.println("过滤之后：" + after);

    }
}
