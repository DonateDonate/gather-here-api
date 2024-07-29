package gather.here.api.infra.config;
import gather.here.api.application.service.MemberService;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.infra.security.CryptoFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public MemberService memberService(MemberRepository memberRepository, CryptoFactory cryptoFactory){
        return new MemberService(memberRepository,cryptoFactory);
    }

    @Bean
    public CryptoFactory cryptoFactory(PasswordEncoder passwordEncoder){
        return new CryptoFactoryImpl(passwordEncoder);
    }

}
