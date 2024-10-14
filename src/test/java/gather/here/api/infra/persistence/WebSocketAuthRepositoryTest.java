package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.domain.service.LocationShareService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
class WebSocketAuthRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthRepositoryTest.class);

    @Autowired
    WebSocketAuthRepository webSocketAuthRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    LocationShareService locationShareService;

    @Test
    @DisplayName("sut는 성공적으로 webSocketAuth를 저장한다")
    public void saveTest(){
        //arrange
        WebSocketAuthRepository sut = new WebSocketAuthRedisTemplateRepositoryImpl(redisTemplate);
        Random random = new Random();
        Long memberSeq = random.nextLong();
        String sessionId = String.valueOf(UUID.randomUUID());
        WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq,sessionId);

        //act
        sut.save(webSocketAuth);
        Optional<WebSocketAuth> actual = sut.findMemberSeq(memberSeq);

        //assert
        Assertions.assertThat(actual.isPresent()).isTrue();
        Assertions.assertThat(actual.get().getMemberSeq()).isEqualTo(memberSeq);
        Assertions.assertThat(actual.get().getSessionId()).isEqualTo(sessionId);
    }

    @Test
    @DisplayName("sut는 성공적으로 sessionId로 webSocketAuth를 가져온다")
    public void findBySessionIdTest(){
        //arrange
        WebSocketAuthRepository sut = new WebSocketAuthRedisTemplateRepositoryImpl(redisTemplate);
        Random random = new Random();
        Long memberSeq = random.nextLong();
        String sessionId = String.valueOf(UUID.randomUUID());
        WebSocketAuth webSocketAuth = WebSocketAuth.create(memberSeq,sessionId);

        //act
        sut.save(webSocketAuth);
        WebSocketAuth actual = sut.getBySessionId(sessionId);

        //assert
        Assertions.assertThat(actual.getMemberSeq()).isEqualTo(memberSeq);
        Assertions.assertThat(actual.getSessionId()).isEqualTo(sessionId);
    }





}