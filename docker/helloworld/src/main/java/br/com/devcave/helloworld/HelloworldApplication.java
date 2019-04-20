package br.com.devcave.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@SpringBootApplication
public class HelloworldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloworldApplication.class, args);
    }


    @GetMapping("/")
    public String helloworld() {
        return "Hello world";
    }

    @GetMapping("/test")
    public String getTestValue(@RequestParam(value = "url") String url) throws InterruptedException {
        Runnable runnable =
                () -> {
                    testingURL(url);
                };

        Thread thread = new Thread(runnable);
        thread.start();

        return "test-value";
    }

    private void testingURL(@RequestParam("url") String url) {
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.currentThread().sleep(1000L);
                HttpHeaders headers = new HttpHeaders();

                HttpEntity httpEntity = new HttpEntity(headers);
                ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, httpEntity, String.class);
                System.out.println("M=getTestValue, response=" + response.getBody());
            } catch (Exception e) {
                System.out.println("M=getTestValue, erro=" + e.getMessage());
            }
        }
    }
}
