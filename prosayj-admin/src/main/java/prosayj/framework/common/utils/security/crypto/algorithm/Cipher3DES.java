package prosayj.framework.common.utils.security.crypto.algorithm;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class Cipher3DES {

    private static ThreadLocal<Base64> base64Holder = new ThreadLocal<Base64>() {
        @Override
        protected Base64 initialValue() {
            return new Base64();
        }
    };
    /**
     * 定义 密钥类型
     */
    private static final String KEY_TYPE = "DESede";

    /**
     * 定义加密算法，DESede，工作模式：ECB，填充方式：PKCS5Padding
     */
    private static final String ALGORITHM = "DESede/ECB/PKCS5Padding";

    /**
     * keybyte为加密密钥，长度为24字节
     * src为被加密的数据缓冲区（源）
     */
    public static byte[] encrypt(byte[] keybyte, byte[] src) throws Exception {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, KEY_TYPE);
            // 加密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            throw new Exception("Cipher3DES-encrypt() error:" +
                    "未知的解密算法");
        } catch (javax.crypto.NoSuchPaddingException e2) {
            throw new Exception("Cipher3DES-encrypt() error:" +
                    "未知的填充方式");
        } catch (Exception e3) {
            throw new Exception("Cipher3DES-encrypt() error:",
                    e3);
        }
    }

    /**
     * keybyte为加密密钥，长度为24字节
     * src为加密后的缓冲区
     */
    public static byte[] decrypt(byte[] keybyte, byte[] src) throws Exception {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, KEY_TYPE);
            // 解密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            throw new Exception("Cipher3DES-decrypt() error:" +
                    "未知的解密算法");
        } catch (javax.crypto.NoSuchPaddingException e2) {
            throw new Exception("Cipher3DES-decrypt() error:" +
                    "未知的填充方式");
        } catch (Exception e3) {
            throw new Exception("Cipher3DES-decrypt() error:",
                    e3);
        }
    }

    public static byte[] decryptBase64(byte[] keybyte, byte[] src) throws Exception {

        if (src != null) {
            src = base64Holder.get().decode(src);
        }

        return decrypt(keybyte, src);
    }

    public static byte[] encryptBase64(byte[] keybyte, byte[] src) throws Exception {
        byte[] ret = encrypt(keybyte, src);

        if (ret != null) {
            ret = base64Holder.get().encode(ret);
        }
        return ret;
    }


    public static String byteArr2HexStr(byte[] bytea) throws Exception {
        String sHex = "";
        int iUnsigned = 0;
        StringBuffer sbHex = new StringBuffer();
        for (int i = 0; i < bytea.length; i++) {
            iUnsigned = bytea[i];
            if (iUnsigned < 0) {
                iUnsigned += 256;
            }
            if (iUnsigned < 16) {
                sbHex.append("0");
            }
            sbHex.append(Integer.toString(iUnsigned, 16));
        }
        sHex = sbHex.toString();
        return sHex;
    }

    public static void main(String[] args) throws Exception {

        byte[] keyByte = "password12345678password".getBytes();
        byte[] srcByte = "passwordpassword".getBytes();
        byte[] encoded = encrypt(keyByte, srcByte);
        System.out.println(new String(byteArr2HexStr(encoded)));
        System.out.println(new String(byteArr2HexStr("password1245678mytest".getBytes())));
        System.out.println();

    }
}
