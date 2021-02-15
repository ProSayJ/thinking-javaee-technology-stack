package prosayj.framework.common.utils.security.crypto.service;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import prosayj.framework.common.utils.security.crypto.constants.EncryptType;
import prosayj.framework.common.utils.security.crypto.constants.SensitiveInfoConstants;
import prosayj.framework.common.utils.security.crypto.factoty.AlgorithmFactory;
import prosayj.framework.common.utils.security.crypto.vo.SecurityKeyInfo;


/**
 * 根据密文及keyInfo选择不同的策略，对密文实现加解密
 *
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class CipherAlgorithmContext {

    private static Logger logger = LoggerFactory.getLogger(CipherAlgorithmContext.class);


    /**
     * @param cipherContent 密文
     * @param keyInfo       data key的信息，若为空，代表未在数据库中存储密钥（目前有DES和3DES2KEY），只根据密文前缀判断使用之前的哪个密钥哪种方式做加解密。
     * @return 解密成功，返回明文，失败，返回原文
     * @Description: 根据keyInfo解密
     * @author princezhaoyt
     */
    public static String decryptByKeyInfo(String cipherContent, SecurityKeyInfo keyInfo) throws Exception {
        CipherAlgorithm cipher = AlgorithmFactory.getInstance(keyInfo.getEncryptType());
        //去除前缀，得到真正密文
        String realCiphertext = cipherContent.replace(keyInfo.getPrefix(), "");
        String realText = cipher.decrypt(realCiphertext, keyInfo);
        //返回之前的密文，解决部分解密失败的时候，返回不带前缀密文的bug
        if (StringUtils.equals(realCiphertext, realText)) {
            logger.error("解密失败，返回原文：{}", cipherContent);
            return cipherContent;
        }
        return realText;
    }

    /**
     * @param content 明文
     * @param keyInfo datakey的信息
     * @return 密文
     * @Description: 根据keyInfo加密
     */
    public static String encryptByKeyInfo(String content, SecurityKeyInfo keyInfo) throws Exception {
        CipherAlgorithm cipher = AlgorithmFactory.getInstance(keyInfo.getEncryptType());
        if (cipher == null) {
            logger.error("加密算法获取失败，加密失败");
            return content;
        }
        String cipherContent = cipher.encrypt(content, keyInfo);
        if (StringUtils.isNotBlank(cipherContent) && !StringUtils.equals(content, cipherContent)) {
            //返回密文时，添加前缀标志
            return keyInfo.getPrefix() + cipherContent;
        }
        return cipherContent;
    }

    /**
     * @param cipherText 密文
     * @return 加密方式
     * @Description: 传入密文，根据密文前缀判断使用的何种加密方式
     * @author zyt
     */
    public static EncryptType getEncryptType(String cipherText) {
        if (StringUtils.isNotEmpty(cipherText)) {
            if (cipherText.startsWith(SensitiveInfoConstants.ENCRYPT_MARK_3DES_3KEY)) {
                return EncryptType.THREE_DES_THREE_KEYS;
            } else if (cipherText.startsWith(SensitiveInfoConstants.ENCRYPT_MARK_SM4)) {
                return EncryptType.SM4;
            } else if (StringUtils.contains(cipherText, SensitiveInfoConstants.ENCRYPT_COMMON_MARK)) {
                logger.error("未知的加密方式，无法解密");
            }
        }
        return null;
    }

}
