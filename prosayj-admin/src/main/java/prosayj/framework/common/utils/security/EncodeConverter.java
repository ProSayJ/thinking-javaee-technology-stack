
package prosayj.framework.common.utils.security;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.util.Base64Utils;


/**
 * 编码方式转换
 *
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class EncodeConverter {
    /**
     * 十六进制转base64编码
     *
     * @param hexStr 待转换的十六进制字符串
     * @return String
     */
    public static String hex2Base64(String hexStr) {
        return Base64Utils.encodeToString(Hex.decode(hexStr));
    }

    /**
     * base64编码转十六进制
     *
     * @param base64Str 待转换的base64编码字符串
     * @return String
     */
    public static String base642Hex(String base64Str) {
        return Hex.toHexString(base64Str.getBytes());
    }
}
