package br.com.devcave.workshop.kubernetes.preferences.client;

import br.com.devcave.workshop.kubernetes.preferences.domain.response.BookResponse;
import br.com.devcave.workshop.kubernetes.preferences.domain.response.CharacterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "proxy-client", url = "${url.proxy}")
public interface ProxyClient {

    @GetMapping("/books/{id}")
    BookResponse getBook(@PathVariable Long id);

    @GetMapping("/characters/{id}")
    CharacterResponse getCharacter(@PathVariable Long id);

    @GetMapping("/ping")
    String ping();
}
