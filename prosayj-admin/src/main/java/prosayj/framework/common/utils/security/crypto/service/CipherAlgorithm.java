package prosayj.framework.common.utils.security.crypto.service;


import prosayj.framework.common.utils.security.crypto.vo.SecurityKeyInfo;

/**
 * 加解密接口类，不同的加密方式，加密途径类请实现此接口
 *
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public interface CipherAlgorithm {


    /**
     * decrypt
     *
     * @param cipherContent 密文
     * @param keyInfo       秘钥信息
     * @return
     * @throws Exception
     */
    String decrypt(String cipherContent, SecurityKeyInfo keyInfo) throws Exception;

    /**
     * encrypt
     *
     * @param content 明文
     * @param keyInfo 秘钥信息
     * @return
     * @throws Exception
     */
    String encrypt(String content, SecurityKeyInfo keyInfo) throws Exception;
}
