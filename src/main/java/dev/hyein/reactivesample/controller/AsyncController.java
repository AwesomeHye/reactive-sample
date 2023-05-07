package dev.hyein.reactivesample.controller;

import dev.hyein.reactivesample.service.AsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class AsyncController {
    private final AsyncService asyncService;

    @GetMapping("/async")
    public String async() {
        Integer id = 1;
        log.info("/async");
        return asyncService.doAsync(id);
    }
}
