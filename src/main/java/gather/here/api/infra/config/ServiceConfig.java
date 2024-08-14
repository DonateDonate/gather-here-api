package gather.here.api.infra.config;
import gather.here.api.application.service.FileService;
import gather.here.api.application.service.MemberService;
import gather.here.api.application.service.RoomService;
import gather.here.api.application.service.TokenService;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RefreshTokenRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.domain.security.AccessTokenFactory;
import gather.here.api.domain.security.RefreshTokenFactory;
import gather.here.api.infra.crypto.CryptoFactoryImpl;
import gather.here.api.infra.security.AccessTokenFactoryImpl;
import gather.here.api.infra.security.RefreshTokenFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public RoomService roomService(
            MemberRepository memberRepository,
        RoomRepository roomRepository
    ){
        return new RoomService(memberRepository,roomRepository);
    }

    @Bean
    public FileService fileService(){
        return new FileService();
    }

    @Bean
    public MemberService memberService(
            MemberRepository memberRepository,
            CryptoFactory cryptoFactory,
            FileService fileService
            ){
        return new MemberService(memberRepository,cryptoFactory,fileService);
    }

    @Bean
    public TokenService tokenService(
            AccessTokenFactory accessTokenFactory,
            RefreshTokenFactory refreshTokenFactory
    ){
        return new TokenService(accessTokenFactory, refreshTokenFactory);
    }

    @Bean
    public CryptoFactory cryptoFactory(PasswordEncoder passwordEncoder){
        return new CryptoFactoryImpl(passwordEncoder);
    }

    @Bean
    public AccessTokenFactory accessTokenFactory(){
        return new AccessTokenFactoryImpl();
    }


    @Bean
    public RefreshTokenFactory refreshTokenFactory(RefreshTokenRepository repository){
        return new RefreshTokenFactoryImpl(repository);
    }
}
