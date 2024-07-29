package gather.here.api.domain.security;

public interface CryptoFactory {
     String passwordEncoder(String password);
     Boolean passwordMatches(String newPassword, String originPassword);
}
