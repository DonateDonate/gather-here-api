package gather.here.api.infra.crypto;

import gather.here.api.domain.security.CryptoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CryptoFactoryImpl implements CryptoFactory {
    private final PasswordEncoder passwordEncoder;

    public String passwordEncoder(String password){
        return passwordEncoder.encode(password);
    }

    public Boolean passwordMatches(String newPassword, String originPassword){
        return passwordEncoder.matches(newPassword, originPassword);
    }
}
