import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import javax.crypto.SecretKey;

public class EllipticalCurveKeyStore {
    public KeyStore keyStore;
    public String filename;
    public char[] pwdArray;

    public EllipticalCurveKeyStore(String filename, String password) throws KeyStoreException {
        this.keyStore = KeyStore.getInstance("pkcs12");
        this.filename = filename;
        this.pwdArray = password.toCharArray();
    }

    public void store() throws KeyStoreException, IOException, NoSuchAlgorithmException,
            CertificateException {
        FileOutputStream fos = new FileOutputStream(filename);

        this.keyStore.store(fos, this.pwdArray);
    }

    public void load() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        FileInputStream fis = new FileInputStream(filename);

        this.keyStore.load(fis, this.pwdArray);
    }

    public void save(PrivateKey privateKey) throws KeyStoreException {
        SecretKey secretKey = EllipticalCurveKey.convertToSecretKey(privateKey);
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(pwdArray);

        this.keyStore.setEntry("private-key", secretKeyEntry, protectionParameter);
    }

    public PrivateKey read() throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(pwdArray);
        KeyStore.Entry entry = this.keyStore.getEntry("private-key", protectionParameter);

        if (!(entry instanceof KeyStore.SecretKeyEntry)) {
            return null;
        }

        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) entry;
        SecretKey secretKey = secretKeyEntry.getSecretKey();

        return EllipticalCurveKey.convertToPrivateKey(secretKey);
    }
}
