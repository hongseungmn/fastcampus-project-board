package com.example.boardproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing//EnableJpaAuditing기능을 추가 활성화
@Configuration//JpaConfig는 Configuration Bean이 된다(설정을 잡는다)
public class JpaConfig {

    @Bean//AuditingFields 에서 값을 설정하는데 CreatedBy 와 같은 것은 기본값을 넣어줄 수 없기 때문에 아래와 같이 초기화를 해주는 것
    public AuditorAware<String> auditorAware() {//따라서 JpaAuditing을 할 때마다 사람이름은 아래 설정한 이름이 들어가게 된다
        return () -> Optional.of("hong"); // TODO: 스프링 시큐리티로 인증 기능을 붙이게 될 때, 수정하자
    }

}
