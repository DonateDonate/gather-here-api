package gather.here.api.domain.dobules;

import gather.here.api.domain.security.CryptoFactory;

public class CryptoFactoryStub implements CryptoFactory {
    @Override
    public String passwordEncoder(String password) {
        return "";
    }

    @Override
    public Boolean passwordMatches(String newPassword, String originPassword) {
        return null;
    }
}
