package gather.here.api.infra.config;
import gather.here.api.application.service.MemberService;
import gather.here.api.domain.repositories.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public MemberService memberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder){
        return new MemberService(memberRepository,passwordEncoder);
    }

}
