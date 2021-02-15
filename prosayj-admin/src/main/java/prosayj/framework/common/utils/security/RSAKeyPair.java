package prosayj.framework.common.utils.security;

public class RSAKeyPair {
    private byte[] publicKey;
    private byte[] privateKey;


    public RSAKeyPair(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;

    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }
}
