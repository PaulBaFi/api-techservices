import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;

/** Runtime helper: the password is read from the environment, never from arguments. */
public final class TruststoreInitializer {
    public static void main(String[] args) throws Exception {
        String encoded = required("AIVEN_CA_CERT_BASE64");
        char[] password = required("AIVEN_TRUSTSTORE_PASSWORD").toCharArray();
        Path certificate = Path.of("/tmp/aiven-ca.pem");
        Path truststore = Path.of("/tmp/aiven-ca.p12");
        Files.write(certificate, Base64.getDecoder().decode(encoded));
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, password);
        try (InputStream input = Files.newInputStream(certificate)) {
            Certificate ca = factory.generateCertificate(input);
            keyStore.setCertificateEntry("aiven-ca", ca);
        }
        try (var output = Files.newOutputStream(truststore)) { keyStore.store(output, password); }
        java.util.Arrays.fill(password, '\0');
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) throw new IllegalStateException(name + " es requerido");
        return value;
    }
}
