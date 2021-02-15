package prosayj.framework.common.utils.security.crypto.util;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;
import prosayj.framework.common.utils.security.crypto.algorithm.Encrypt;
import prosayj.framework.common.utils.security.crypto.constants.EncryptType;
import prosayj.framework.common.utils.security.crypto.constants.SensitiveInfoConstants;
import prosayj.framework.common.utils.security.crypto.factoty.AlgorithmFactory;
import prosayj.framework.common.utils.security.crypto.service.CipherAlgorithmContext;
import prosayj.framework.common.utils.security.crypto.vo.SecurityKeyInfo;

import java.lang.reflect.Field;
import java.util.List;


/**
 * 敏感信息加解密工具类
 *
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class SensitiveInfoUtils {

    private static Logger logger = LoggerFactory.getLogger(SensitiveInfoUtils.class);


    /**
     * 校验传入字符串是否已经加密：
     * 如果已经加密，则不做任何操作，直接返回
     * 如果未加密，使用当前的加密算法及密钥进行加密，返回密文
     *
     * @param content 需要判断并加密的字符串
     * @return 如果需要加密，则返回加密后的字符串，否则，返回原字符串
     */
    public static String smartEncryptWithCheck(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        if (isNeedShield()) {
            return content;
        }
        if (StringUtils.contains(content, SensitiveInfoConstants.ENCRYPT_COMMON_MARK)) {
            //若已经是密文
            return content;
        } else {
            String cipherContent = smartEncrypt(content);
            return cipherContent;
        }
    }


    /**
     * 判断是否应该解密，并对传入字符串进行解密操作：
     * 如果是hibernate在插入等操作的拼接sql阶段调用的解密，则不去做解密，返回原文
     * 如果是其他地方如 dao等），则对传入的字符串进行解密操作并返回
     *
     * @param ciphertext 需要判断并解密的字符串
     * @return 如果需要解密，则返回解密后的字符串，否则，返回原字符串
     */
    public static String smartDecryptWithCheck(String ciphertext) {
        if (StringUtils.isBlank(ciphertext)) {
            return ciphertext;
        }
        if (isNeedShield()) {
            return ciphertext;
        }
        String text = ciphertext;
        while (StringUtils.contains(text, SensitiveInfoConstants.ENCRYPT_COMMON_MARK)) {
            // 增加临时变量，每次对解密返回的值判断是否与解密前一致，若是，解密失败，返回之前的密文
            // 解决多重加密情况下，解密失败后，循环解密问题
            String tmpText = text;
            text = smartDecrypt(text);
            if (StringUtils.equals(tmpText, text)) {
                return ciphertext;
            }
        }
        return text;
    }

    /**
     * 是否需要屏蔽加解密（即返回原文）
     * 1 若hibernate / ibatis底层调用。根据AbstractEntityTuplizer类名校验，则需要屏蔽。否则在hibernate最后持久化到数据库，拼接sql的
     * 时候，调用get返回明文，持久化加密将失效
     *
     * @return
     */
    private static boolean isNeedShield() {
        StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
        if (stackElements != null) {
            for (StackTraceElement ste : stackElements) {
                if (ste.getClassName().contains("AbstractEntityTuplizer") ||
                        ste.getClassName().contains("ibatis.reflection")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 智能加密，获取正在使用的密钥，以及加密方式，为明文进行加密
     * 若加密失败（密钥不存在等原因），返回明文
     *
     * @param content 明文
     * @return 密文
     */
    public static String smartEncrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        SecurityKeyInfo keyInfo = AlgorithmFactory.get3DESKeyInfo();
        keyInfo.setSystem(true);
        if (StringUtils.isNotBlank(keyInfo.getKeyContent())) {
            try {
                return CipherAlgorithmContext.encryptByKeyInfo(content, keyInfo);
            } catch (Exception e) {
                handleException(e);
                logger.error("加密失败，返回原文，原因：", e);
                return content;
            }
        } else {
            logger.error("当前使用密钥为空，加密失败，返回原文");
            return content;
        }
    }

    /**
     * 对对象的某些属性进行3DES加密，密钥为3KEY长度 ，若传入属性为空，则使用默认的属性加密 .zyt
     *
     * @param obj     对象
     * @param keyInfo 当前密钥信息
     * @param fields  需要加密的属性
     * @throws Exception
     */
    private static void encrypt3DES3KEYS(Object obj, SecurityKeyInfo keyInfo, String[] fields) throws Exception {
        if (obj != null) {

            if (fields == null || fields.length == 0) {
                fields = SensitiveInfoConstants.DEFAULT_FIELDS;
            }

            for (int i = 0; i < fields.length; i++) {
                Field field = ReflectionUtils.findField(obj.getClass(), fields[i]);
                field.setAccessible(true);
                Object val = field.get(obj);
                // 加密数据
                if (val == null) {
                    continue;
                }
                val = CipherAlgorithmContext.encryptByKeyInfo(val.toString(), keyInfo);
                ReflectionUtils.setField(field, obj, val);
            }
        }
    }

    /**
     * 对字符串密文智能解密 根据密文前缀，判断何种加密方式 zyt
     *
     * @param ciphertext 密文
     * @return 明文，若未识别加密方式，则返回原文
     */
    public static String smartDecrypt(String ciphertext) {
        if (StringUtils.isBlank(ciphertext)) {
            return ciphertext;
        }
        EncryptType encrypt = CipherAlgorithmContext.getEncryptType(ciphertext);
        if (encrypt == null) {
            return ciphertext;
        }

        if (encrypt == EncryptType.THREE_DES_THREE_KEYS) {
            //3des 3key加密的前缀
            //String encryptVersion = getKeyVersionByCipherText(ciphertext, encrypt);
            SecurityKeyInfo keyInfo = AlgorithmFactory.get3DESKeyInfo();
            keyInfo.setSystem(true);
            if (StringUtils.isBlank(keyInfo.getKeyContent())) {
                logger.error("未找到对应的密钥，解密失败，返回原文");
                return ciphertext;
            }
            try {
                return CipherAlgorithmContext.decryptByKeyInfo(ciphertext, keyInfo);
            } catch (Exception e) {
                handleException(e);
                logger.error("智能解密失败，返回原文，原因：", e);
                return ciphertext;
            }
        }
        return ciphertext;
    }

    ;

    /**
     * 对对象的某些属性智能解密，若传入为空，则使用默认字段对对象加密
     * 根据密文前缀，判断何种加密方式 zyt
     *
     * @param obj
     * @param fields
     */
    public static void smartDecrypt(Object obj, String[] fields) {
        if (obj != null) {

            if (fields == null || fields.length == 0) {
                fields = SensitiveInfoConstants.DEFAULT_FIELDS;
            }

            for (int i = 0; i < fields.length; i++) {
                Field field = ReflectionUtils.findField(obj.getClass(), fields[i]);
                try {
                    field.setAccessible(true);
                    Object val = field.get(obj);
                    if (val == null) {
                        continue;
                    }
                    val = smartDecrypt(val.toString());
                    ReflectionUtils.setField(field, obj, val);
                } catch (Exception e) {
                    handleException(e);
                    logger.error("智能解密对象失败，原因：", e);
                }
            }
        }
    }

    /**
     * 对一个集合中所有的对象的某些属性智能解密，若传入为空，则使用默认字段对对象加密
     * 根据密文前缀，判断何种加密方式  zyt
     *
     * @param list
     * @param fields
     */
    public static void smartDecryptList(List list, String[] fields) {
        if (list == null || list.size() < 1) {
            return;
        }
        for (Object obj : list) {
            smartDecrypt(obj, fields);
        }

    }

    /**
     * 根据密文和加密方式，获取密文使用的加密方式 zyt
     *
     * @param cipherText  密文
     * @param encryptType 加密方式
     * @return 密钥版本
     */
    private static String getKeyVersionByCipherText(String cipherText, EncryptType encryptType) {
        String encryptVersion = "";
        if (encryptType == EncryptType.THREE_DES_THREE_KEYS) {
            encryptVersion = cipherText.substring(SensitiveInfoConstants.ENCRYPT_MARK_3DES_3KEY.length(),
                    cipherText.indexOf("_" + SensitiveInfoConstants.ENCRYPT_COMMON_MARK));
        }

        return encryptVersion;
    }

    /**
     * 加解密异常时候的处理 ,是否需要发送短信通知出错？？？？？ zyt
     *
     * @param e
     */
    private static void handleException(Exception e) {
        //SensitiveInfoDictionaryUtils.sendExceptionSms(e);
    }

    public static void main(String[] args) throws Exception {
        Long s = System.currentTimeMillis();
        String bankCard = "藏\",\"contactPhone\":\"13812348888\",\"email\":\"1234@qq.com\",\"messagePhone\":\"15812349900\",\"phone\":\"13812348888\",\"useMessager\":\"1\"}";
        System.out.println("明文长度：" + bankCard.length());
        String encBankCard = SensitiveInfoUtils.smartEncryptWithCheck(bankCard);
        System.out.println("密文：" + encBankCard);
        System.out.println("密文长度：" + encBankCard.length());
        String realBankCard = SensitiveInfoUtils.smartDecryptWithCheck(encBankCard);
        System.out.println("明文：" + realBankCard);
        System.out.println(LoggerMaskUtils.maskString(realBankCard));

        System.out.println(SensitiveInfoUtils.smartDecryptWithCheck("3DES3KEY_ENCRPYT_MARK_57LK65qwlqc/9V31dKMJIg=="));
        System.out.println("时间" + (System.currentTimeMillis() - s));

        //1、生成 - mastKey
        Encrypt encrypt = new Encrypt();
        System.out.println(encrypt.encrypt("abc@2019"));
        //

    }

}
