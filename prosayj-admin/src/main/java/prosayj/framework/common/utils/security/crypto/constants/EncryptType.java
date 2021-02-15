package prosayj.framework.common.utils.security.crypto.constants;

import java.io.Serializable;

/**
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public enum EncryptType implements Serializable {
    /**
     * 3DES 3key 加密
     */
    THREE_DES_THREE_KEYS,
    /**
     * 国密4
     */
    SM4;
}
