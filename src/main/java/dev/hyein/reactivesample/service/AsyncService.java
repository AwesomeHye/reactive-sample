package dev.hyein.reactivesample.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AsyncService {
    RestTemplate rt = new RestTemplate();

    @Async
    public String doAsync(Integer id) {
        String response = rt.getForObject("http://localhost:8080/mock/{id}", String.class, id);
        log.info(response);
        return response;
    }
}
