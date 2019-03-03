package br.com.devcave.workshop.kubernetes.proxy.client;

import br.com.devcave.workshop.kubernetes.proxy.response.BookResponse;
import br.com.devcave.workshop.kubernetes.proxy.response.CharacterResponse;
import br.com.devcave.workshop.kubernetes.proxy.response.HouseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ice-and-fire-client", url = "${url.ice-and-fire}")
public interface IceAndFireClient {

    @GetMapping("/books/{id}")
    BookResponse getBook(@PathVariable("id") Long id);

    @GetMapping("/characters/{id}")
    CharacterResponse getCharacter(@PathVariable("id") Long id);

    @GetMapping("/houses/{id}")
    HouseResponse getHouse(@PathVariable("id") Long id);
}
