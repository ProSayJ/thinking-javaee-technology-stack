package prosayj.framework.common.utils.security.crypto.test;


import prosayj.framework.common.utils.security.crypto.util.SensitiveInfoUtils;

/**
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class Test {
    public static void main(String[] args) {
        //加密
        System.out.println(SensitiveInfoUtils.smartEncrypt("755936018310101"));

        //解密
        System.out.println(SensitiveInfoUtils.smartDecryptWithCheck("3DES3KEY_ENCRPYT_MARK_Cn9HSu9DIz+FxHL5Vng5Ow=="));


    }
}
