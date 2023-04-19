import java.security.PublicKey;
import java.util.Base64;

public class EmailParser {
    public static String exportPublicKey(PublicKey publicKey) {
        byte[] encodedPublicKey = publicKey.getEncoded();

        return Base64.getEncoder().encodeToString(encodedPublicKey);
    }

    public static PublicKey importPublicKey(String rawPublicKey) {
        byte[] decodedPublicKey = Base64.getDecoder().decode(rawPublicKey);

        return new PublicKey() {
            @Override
            public String getAlgorithm() {
                return "ECDSA";
            }

            @Override
            public String getFormat() {
                return "X.509";
            }

            @Override
            public byte[] getEncoded() {
                return decodedPublicKey;
            }
        };
    }

    public static String exportSignature(byte[] signature) {
        return Base64.getEncoder().encodeToString(signature);
    }

    public static byte[] importSignature(String rawSignature) {
        byte[] decodedSignature = Base64.getDecoder().decode(rawSignature);

        return decodedSignature;
    }
}
