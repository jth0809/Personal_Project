package com.personal.backend.config.data;

import com.personal.backend.domain.Category;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final Dataproperties dataproperties;

    /**
     * 스프링 부트 애플리케이션이 시작될 때 이 run 메서드가 자동으로 실행됩니다.
     * @param args 애플리케이션 실행 시 전달된 커맨드 라인 인자(arguments)
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. 실행 인자 중에 "--init-data"가 포함되어 있는지 확인합니다.
        if (Arrays.asList(args).contains("--init-data")) {
            log.info("Command line argument '--init-data' detected. Starting data initialization...");

            // 2. 멱등성을 보장하기 위해 데이터가 없을 때만 삽입합니다.
            if (userRepository.findByEmail("admin@test.com").isEmpty()) {
                userRepository.save(User.builder()
                        .email("admin@test.com")
                        .password(passwordEncoder.encode(dataproperties.password()))
                        .username("관리자")
                        .role(UserRole.ADMIN)
                        .build());
                log.info("Admin user created.");
            }

            if (productRepository.findByName("고성능 노트북").isEmpty()) {
                productRepository.save(new Product("고성능 노트북", "최신 M4 칩이 탑재된 노트북입니다.", 1500000, "",new Category("컴퓨터")));
                productRepository.save(new Product("기계식 키보드", "타건감이 뛰어난 기계식 키보드입니다.", 120000, "",new Category("컴퓨터")));
                log.info("Product data created.");
            }
            
            log.info("Data initialization finished.");
        } else {
            log.info("No '--init-data' argument found. Skipping data initialization.");
        }
    }
}
