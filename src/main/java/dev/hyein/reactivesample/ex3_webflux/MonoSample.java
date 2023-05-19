package dev.hyein.reactivesample.ex3_webflux;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

@Slf4j
public class MonoSample {
    public static void main(String[] args) {
        URI woridTimeUri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("worldtimeapi.org")
                .port(80)
                .path("/api/timezone/Asia/Seoul")
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Mono.just(restTemplate.exchange(woridTimeUri, HttpMethod.GET, new HttpEntity<>(headers), String.class))
                .map(response -> {
                    JSONObject jsonObject = new ObjectMapper().convertValue(response.getBody(), JSONObject.class);
                    return jsonObject.get("datetime");
                })
                .subscribe(
                        emitData -> log.info("emitData: {}", emitData),
                        error -> log.error(error.getMessage()),
                        () -> log.info("onComplete Finished") // onComplete 추가하면 얘가, 안 하면 emitData 라인이 호출된다.
                );

    }
}
