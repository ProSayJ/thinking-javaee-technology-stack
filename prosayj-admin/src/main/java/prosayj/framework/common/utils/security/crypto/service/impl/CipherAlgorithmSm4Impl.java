package prosayj.framework.common.utils.security.crypto.service.impl;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import prosayj.framework.common.utils.security.crypto.algorithm.Sm4;
import prosayj.framework.common.utils.security.crypto.service.CipherAlgorithm;
import prosayj.framework.common.utils.security.crypto.vo.SecurityKeyInfo;


/**
 *
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class CipherAlgorithmSm4Impl implements CipherAlgorithm {


    private static Logger logger = LoggerFactory.getLogger(CipherAlgorithmSm4Impl.class);


    @Override
    public String decrypt(String cipherContent, SecurityKeyInfo keyInfo) throws Exception {
        return Sm4.decryptEcb(keyInfo.getKeyContent(), cipherContent);
    }

    @Override
    public String encrypt(String content, SecurityKeyInfo keyInfo) throws Exception {
        return Sm4.encryptEcb(keyInfo.getKeyContent(), content);
    }
}
