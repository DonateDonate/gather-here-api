package gather.here.api.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class LoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_USERNAME_KEY = "identity";
    public static final String DEFAULT_PASSWORD_KEY = "password";

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/login", "POST");

    private final ObjectMapper objectMapper;
    private String usernameParameter = DEFAULT_USERNAME_KEY;
    private String passwordParameter = DEFAULT_PASSWORD_KEY;
    private boolean postOnly = true;

    public LoginProcessingFilter(ObjectMapper objectMapper) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        if (!request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            throw new AuthenticationServiceException(
                    "Authentication content-type not supported: " + request.getContentType());
        }

        ServletInputStream inputStream = request.getInputStream();
        Map<String, String> usernamePasswordMap = objectMapper.readValue(inputStream, Map.class);

        String username = obtainParameter(usernameParameter, usernamePasswordMap);
        String password = obtainParameter(passwordParameter, usernamePasswordMap);
        log.info("username ={}",username);
        log.info("password ={}",password);

        UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);

        return getAuthenticationManager().authenticate(authRequest);


    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    private String obtainParameter(String parameter, Map<String, String> usernamePasswordMap) {
        String value = usernamePasswordMap.get(parameter);
        if (Objects.isNull(value)) {
            return "";
        }

        return value;
    }
}
