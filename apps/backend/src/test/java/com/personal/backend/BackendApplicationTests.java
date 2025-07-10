package com.personal.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // 👈 import 추가

@SpringBootTest
@ActiveProfiles("test") // 👈 이 어노테이션을 추가하여 'test' 프로파일을 활성화합니다.
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // 이 테스트는 이제 H2 데이터베이스를 사용하여 성공적으로 컨텍스트를 로드합니다.
    }

}
