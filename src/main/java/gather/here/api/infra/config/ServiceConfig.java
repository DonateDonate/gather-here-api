package gather.here.api.infra.config;
import gather.here.api.application.service.MemberService;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.TokenRepository;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.domain.security.JwtFactory;
import gather.here.api.infra.security.CryptoFactoryImpl;
import gather.here.api.infra.security.CustomPasswordEncoder;
import gather.here.api.infra.security.JwtFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public MemberService memberService(
            MemberRepository memberRepository,
            TokenRepository tokenRepository,
            CryptoFactory cryptoFactory,
            JwtFactory jwtFactory
            ){
        return new MemberService(memberRepository,tokenRepository,cryptoFactory,jwtFactory);
    }

    @Bean
    public CryptoFactory cryptoFactory(PasswordEncoder passwordEncoder){
        return new CryptoFactoryImpl(passwordEncoder);
    }

    @Bean
    public JwtFactory jwtFactory(){
        return new JwtFactoryImpl();
    }


}
