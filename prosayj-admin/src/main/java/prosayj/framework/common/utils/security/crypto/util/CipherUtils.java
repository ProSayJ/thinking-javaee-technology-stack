package prosayj.framework.common.utils.security.crypto.util;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import prosayj.framework.common.utils.security.crypto.algorithm.Sm4;
import prosayj.framework.common.utils.security.crypto.constants.EncryptType;
import prosayj.framework.common.utils.security.crypto.constants.SensitiveInfoConstants;
import prosayj.framework.common.utils.security.crypto.factoty.AlgorithmFactory;
import prosayj.framework.common.utils.security.crypto.service.CipherAlgorithm;
import prosayj.framework.common.utils.security.crypto.service.CipherAlgorithmContext;
import prosayj.framework.common.utils.security.crypto.vo.SecurityKeyInfo;

import java.util.Random;

/**
 *
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class CipherUtils {

    private static Logger logger = LoggerFactory.getLogger(CipherUtils.class);

    /**
     * 32位16进制
     */
    public static final int DEFAULT_KEY_SIZE = 32;

    /**
     * gen key
     *
     * @return 32位十六进制字符串
     */
    public static String generateKey() {
        try {
            return Sm4.generateKey();
        } catch (Exception e) {
            logger.error("随机生成秘钥错误，原因：", e);
            return randomHexString(DEFAULT_KEY_SIZE);
        }
    }


    /**
     * encrypt
     *
     * @param encryptType 加密方式 SM4
     * @param key         秘钥 十六进制数据
     * @param content     加密内容
     */
    public static String encrypt(String encryptType, String key, String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        if (StringUtils.contains(content, SensitiveInfoConstants.ENCRYPT_COMMON_MARK)) {
            //若已经是密文
            return content;
        } else {
            SecurityKeyInfo securityKeyInfo = AlgorithmFactory.getInstance(encryptType);
            securityKeyInfo.setKeyContent(key);
            try {
                return CipherAlgorithmContext.encryptByKeyInfo(content, securityKeyInfo);
            } catch (Exception e) {
                logger.error("加密失败，返回原文，原因：" + e);
            }
            return content;
        }
    }

    /**
     * decrypt
     *
     * @param key        秘钥 十六进制数据
     * @param ciphertext 解密内容
     */
    public static String decrypt(String key, String ciphertext) {
        if (StringUtils.isBlank(ciphertext)) {
            return ciphertext;
        }
        EncryptType encrypt = CipherAlgorithmContext.getEncryptType(ciphertext);
        String text = ciphertext;
        while (StringUtils.contains(text, SensitiveInfoConstants.ENCRYPT_COMMON_MARK)) {
            // 增加临时变量，每次对解密返回的值判断是否与解密前一致，若是，解密失败，返回之前的密文
            // 解决多重加密情况下，解密失败后，循环解密问题
            SecurityKeyInfo securityKeyInfo = AlgorithmFactory.getInstance(encrypt.name());
            securityKeyInfo.setKeyContent(key);
            String tmpText = text;
            try {
                text = CipherAlgorithmContext.decryptByKeyInfo(ciphertext, securityKeyInfo);
            } catch (Exception e) {
                logger.error("解密失败，返回原文，原因：", e);
            }
            if (StringUtils.equals(tmpText, text)) {
                return ciphertext;
            }
        }
        return text;
    }


    /**
     * encrypt 根据合约要求：去掉加密数据前缀逻辑
     *
     * @param encryptType 加密方式 SM4
     * @param key         秘钥 十六进制数据
     * @param content     加密内容
     */
    public static String encryptData(String encryptType, String key, String content) throws Exception {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        SecurityKeyInfo keyInfo = AlgorithmFactory.getInstance(encryptType);
        keyInfo.setKeyContent(key);
        CipherAlgorithm cipher = AlgorithmFactory.getInstance(keyInfo.getEncryptType());
        return cipher.encrypt(content, keyInfo);
    }

    /**
     * decrypt 根据合约要求：解密数据
     *
     * @param key        秘钥 十六进制数据
     * @param ciphertext 解密内容
     */
    public static String decryptData(String encryptType, String key, String ciphertext) throws Exception {
        if (StringUtils.isBlank(ciphertext)) {
            return ciphertext;
        }
        SecurityKeyInfo keyInfo = AlgorithmFactory.getInstance(encryptType);
        keyInfo.setKeyContent(key);
        CipherAlgorithm cipher = AlgorithmFactory.getInstance(keyInfo.getEncryptType());
        return cipher.decrypt(ciphertext, keyInfo);
    }


    /**
     * 获取16进制随机数
     *
     * @param len
     * @return
     * @throws
     */
    public static String randomHexString(int len) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < len; i++) {
            result.append(digital[(new Random().nextInt(16))]);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        try {
            String json = "{\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\",\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\",\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"，\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"，\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"，\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"，\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"，\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"，\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"，\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"}";
            // 自定义的32位16进制密钥
            String key = "8EB666B157C4DCB9F19719F09820FE15";
            System.out.println("key：" + Sm4.generateKey());
            System.out.println("key：" + Sm4.generateKey().length());
            System.out.println("key：" + randomHexString(32));
            System.out.println("key：" + randomHexString(32).length());

            String cipher = encrypt("SM4", key, json);
            System.out.println("密文：" + cipher);
            System.out.println("长度：" + cipher.length());
            String json1 = decrypt(key, cipher);
            System.out.println("明文：" + json1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
