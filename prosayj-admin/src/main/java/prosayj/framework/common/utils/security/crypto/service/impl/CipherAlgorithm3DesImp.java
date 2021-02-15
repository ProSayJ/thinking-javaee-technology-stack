package prosayj.framework.common.utils.security.crypto.service.impl;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import prosayj.framework.common.utils.security.crypto.algorithm.Cipher3DES;
import prosayj.framework.common.utils.security.crypto.algorithm.Decrypt;
import prosayj.framework.common.utils.security.crypto.constants.EncryptType;
import prosayj.framework.common.utils.security.crypto.constants.SensitiveInfoConstants;
import prosayj.framework.common.utils.security.crypto.service.CipherAlgorithm;
import prosayj.framework.common.utils.security.crypto.vo.SecurityKeyInfo;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 敏感信息加密类
 *
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class CipherAlgorithm3DesImp implements CipherAlgorithm {

    private static final String ENCODE_TYPE = "utf-8";


    private static Logger logger = LoggerFactory.getLogger(CipherAlgorithm3DesImp.class);

    /**
     * @param content 需要加密的内容
     * @param key     密钥信息
     * @return 密文
     * @Description: 使用规定的密钥进行3DES 3KEY加密
     */
    public static String encrypt3DES3KEY(String content, String key) throws Exception {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(key)) {
            logger.error("明文或data key为空，解密失败");
            return content;
        }
        try {
            byte[] bs = Cipher3DES.encryptBase64(get3DESBytes(key), content.getBytes(ENCODE_TYPE));
            return new String(bs, ENCODE_TYPE);
        } catch (UnsupportedEncodingException e) {
            logger.error("加密失败，原因：", e);
            throw new Exception("CipherAlgorithmWithCode-encrypt3DES3KEY() error:" + "不支持的编码方式");
        } catch (Exception e1) {
            logger.error("加密失败，原因：", e1);
            throw new Exception("CipherAlgorithmWithCode-encrypt3DES3KEY() error:" +
                    e1.getMessage());
        }
    }

    /**
     * @param ciphertext 密文
     * @param key        密钥信息
     * @return 明文
     * @Description: 使用规定的密钥，对密文进行3DES解密
     * @author zyt
     */
    public static String decrypt3DES3KEY(String ciphertext, String key) throws Exception {
        if (StringUtils.isNotEmpty(ciphertext) && StringUtils.isNotEmpty(key)) {
            try {
                byte[] ciphertextBytes = ciphertext.getBytes(ENCODE_TYPE);
                byte[] certNoByte = Cipher3DES.decryptBase64(get3DESBytes(key), ciphertextBytes);
                return new String(certNoByte, ENCODE_TYPE);
            } catch (UnsupportedEncodingException e) {
                logger.error("解密失败，原因：", e);
                throw new Exception("CipherAlgorithmWithCode-decrypt3DES3KEY() error:" +
                        "不支持的编码方式");
            } catch (Exception e1) {
                logger.error("加密失败，原因：", e1);
                throw new Exception("CipherAlgorithmWithCode-decrypt3DES3KEY() error:" +
                        e1.getMessage());
            }
        } else {
            logger.error("解密失败，原因：密文或data key为空");
            throw new Exception("CipherAlgorithmWithCode-decrypt3DES3KEY() error:" + "密钥或原文为空");
        }
    }


    private static byte[] get3DESBytes(String key) throws Exception {
        try {
            if (key.length() == 24) {
                return key.getBytes(ENCODE_TYPE);
            } else {
                return Arrays.copyOf(key.getBytes(ENCODE_TYPE), 24);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("获取密钥字节失败，不支持的编码方式");
            throw new Exception("CipherAlgorithmWithCode-get3DESBytes() error"
                    + "不支持的编码方式");
        }
    }


    /**
     * @return master key
     * @throws Exception
     * @Description: 获取master key。
     * 暂时从应用服务器获取，后续可能需要修改为从加密机获取
     */
    private static String getMasterKey() throws Exception {
        //TODO 获取mastKey
        String key = "85e5ef56ec73085de68896b9afbb3453";
        try {
            Decrypt d = new Decrypt();
            return d.decrypt(key);
        } catch (Exception e) {
            logger.error("获取master key失败，原因：", e);
            throw new Exception("CipherAlgorithmWithCode-getMasterKey() error"
                    + "decrypt master key failed");
        }
    }

    /**
     * @param cipherKey   data key的密文
     * @param masterKey   master key
     * @param encryptType 加密方式
     * @return data key明文
     * @Description: 为加密的data key进行解密
     * @author zyt
     */
    private static String decryptDataKey(String cipherKey, String masterKey, EncryptType encryptType) throws Exception {
        if (encryptType == EncryptType.THREE_DES_THREE_KEYS) {
            cipherKey = cipherKey.replace(SensitiveInfoConstants.ENCRYPT_MARK_3DES_3KEY
                    + SensitiveInfoConstants.ENCRYPT_COMMON_MARK, "");
            try {
                return decrypt3DES3KEY(cipherKey, masterKey);
            } catch (Exception e) {
                logger.error("解密data key失败，原因：", e);
                throw new Exception("CipherAlgorithmWithCode-decryptDataKey() error:" +
                        e.getMessage());
            }
        } else {
            return cipherKey;
        }
    }


    @Override
    public String decrypt(String cipherContent, SecurityKeyInfo keyInfo) throws Exception {
        String resText;

        //如果获取密钥失败，则返回原字符串
        if (keyInfo != null) {
            String dataKey = keyInfo.getKeyContent();
            if (keyInfo.isSystem()) {
                String masterKey = getMasterKey();
                dataKey = decryptDataKey(keyInfo.getKeyContent(), masterKey, keyInfo.getEncryptType());
            }
            resText = decrypt3DES3KEY(cipherContent, dataKey);
        } else {
            logger.error("data key或master key为空，解密失败，返回原文");
            resText = cipherContent;
        }

        return resText;
    }

    @Override
    public String encrypt(String content, SecurityKeyInfo keyInfo) throws Exception {
        if (keyInfo != null) {
            //TODO 直接加密
            String dataKey = keyInfo.getKeyContent();
            if (keyInfo.isSystem()) {
                String masterKey = getMasterKey();
                if (StringUtils.isBlank(masterKey)) {
                    logger.error("master key为空，加密失败");
                    return content;
                }
                dataKey = decryptDataKey(keyInfo.getKeyContent(), masterKey, keyInfo.getEncryptType());
            }
            return encrypt3DES3KEY(content, dataKey);
        } else {
            logger.error("data key为空，加密失败，返回原文");
            return content;
        }
    }

    public static void main(String[] args) throws Exception {
        //1、生成 - DataKey
        String dateKey = "scm@2019";
        System.out.println(CipherAlgorithm3DesImp.encrypt3DES3KEY(dateKey, getMasterKey()));
    }

}
