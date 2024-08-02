package gather.here.api.global.util;

import gather.here.api.global.exception.AuthException;
import gather.here.api.global.exception.ResponseStatus;
import org.springframework.http.HttpStatus;

public class TokenUtil {

    public static String withTokenPrefix(String token, String prefix) {
        return  prefix+ " " + token;
    }

    public static  String removePrefix(String token, String prefix) {
        if (!token.startsWith(prefix + " ")) {
            throw new AuthException(ResponseStatus.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
        return token.substring(prefix.length() + 1);
    }
}
