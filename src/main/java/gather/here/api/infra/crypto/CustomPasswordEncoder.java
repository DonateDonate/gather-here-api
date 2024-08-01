package gather.here.api.infra.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CustomPasswordEncoder implements PasswordEncoder{

    @Value("${security.salt}")
    private String SALT;

    @Override
    public String encode(CharSequence rawPassword) {

        String saltedPassword = rawPassword.toString() + SALT;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(saltedPassword.getBytes());

            return new String(Hex.encode(hashedBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }

    }
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {

        String hashedPassword = encode(rawPassword);

        return encodedPassword.equals(hashedPassword);
    }
}