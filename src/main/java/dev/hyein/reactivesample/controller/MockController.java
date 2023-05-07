package dev.hyein.reactivesample.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MockController {
    @GetMapping("mock/{id}")
    public String mock(@PathVariable Integer id) {
        return "serviced: " + id;
    }
}
