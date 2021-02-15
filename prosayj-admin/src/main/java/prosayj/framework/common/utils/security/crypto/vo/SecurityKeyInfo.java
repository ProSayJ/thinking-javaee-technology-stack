package prosayj.framework.common.utils.security.crypto.vo;


import prosayj.framework.common.utils.security.crypto.constants.EncryptType;

import java.io.Serializable;

/**
 * 密钥信息
 *
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class SecurityKeyInfo implements Serializable, Cloneable {

    /**
     * @Fields serialVersionUID : version
     */
    private static final long serialVersionUID = 1L;

    /**
     * 加密方式
     */
    private EncryptType encryptType;
    /**
     * 密钥版本
     */
    private String keyVersion;

    /**
     * 密钥密文
     */
    private String keyContent;
    /**
     * 该密钥生成密文的前缀
     */
    private String prefix;

    /**
     * 是否是系统使用
     */
    private boolean isSystem;

    public EncryptType getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(EncryptType encryptType) {
        this.encryptType = encryptType;
    }

    public String getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(String keyVersion) {
        this.keyVersion = keyVersion;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getKeyContent() {
        return keyContent;
    }

    public void setKeyContent(String keyContent) {
        this.keyContent = keyContent;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
