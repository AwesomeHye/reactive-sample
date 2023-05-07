package dev.hyein.reactivesample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DefaultJerseyApplicationPath;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SimpleCallBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleCallBackApplication.class, args);
    }
}
