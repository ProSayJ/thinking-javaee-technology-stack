package prosayj.framework.common.utils.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class RSAUtils {
    public static void main(String[] args) throws UnsupportedEncodingException {
        publickeyEncryptionAndPrivateKeyDecryption("yjyyf1", "utf-8");

    }

    public static final String ALG_RSA = "RSA";
    public static final String KEYPAIR_PUBKEY = "pubKey";
    public static final String KEYPAIR_PRIKEY = "priKey";

    public static final int size = 1024;
    public static final String code_type = "UTF-8";


    /**
     * 生成公私钥对
     *
     * @param keySize
     * @return
     */
    public static RSAKeyPair generateKey(int keySize) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALG_RSA);
            keyPairGen.initialize(keySize, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

            return new RSAKeyPair(publicKey.getEncoded(), privateKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * 加密
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("Public key is empty!");
        }
        try {
            RSAPublicKey pubKey = loadPublicKey(publicKey);
            Cipher cipher = Cipher.getInstance(ALG_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 解密
     *
     * @param cipherData
     * @param privateKey
     * @return
     */
    public static byte[] decryptByPrivateKey(byte[] cipherData, byte[] privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key is empty!");
        }
        try {
            RSAPrivateKey privKey = loadPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(ALG_RSA);
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            return cipher.doFinal(cipherData);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static RSAPublicKey loadPublicKey(byte[] pubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKey);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private static RSAPrivateKey loadPrivateKey(byte[] priKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(priKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALG_RSA);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥加密 私钥解密
     *
     * @param src
     * @param charsetName
     * @throws UnsupportedEncodingException
     */
    public static void publickeyEncryptionAndPrivateKeyDecryption(String src, String charsetName) throws UnsupportedEncodingException {
        System.out.println("src = " + src);
        //生成公私钥对
        RSAKeyPair rsaKeyPair = generateKey(size);
        byte[] privateKey = rsaKeyPair.getPrivateKey();
        byte[] publicKey = rsaKeyPair.getPublicKey();

        System.out.println("publicKey = " + Base64.getEncoder().encodeToString(publicKey));
        System.out.println("privateKey = " + Base64.getEncoder().encodeToString(privateKey));

        //公钥加密:
        byte[] msgSecurity = RSAUtils.encryptByPublicKey(new String(src.getBytes(), charsetName == null ? code_type : charsetName).getBytes(), publicKey);
        System.out.println("公钥加密 = " + Base64.getEncoder().encodeToString(msgSecurity));

        //私钥解密
        byte[] msgDecode = RSAUtils.decryptByPrivateKey(msgSecurity, privateKey);

        System.out.println("私钥解密 = " + new String(msgDecode, Charset.forName(charsetName == null ? code_type : charsetName)));


        //公钥网络传输
        String pubKeyBase64Str = Base64.getEncoder().encodeToString(publicKey);
        System.out.println("pubKeyBase64Str = " + pubKeyBase64Str);
        //公钥网络传输
        String pubKeyencode = URLEncoder.encode(pubKeyBase64Str, code_type);
        System.out.println("pubKeyencode = " + pubKeyencode);

        //公钥网络还原
        String pubkeySrc = URLDecoder.decode(pubKeyencode, code_type);
        System.out.println("pubkeySrc = " + pubkeySrc);

    }

}



