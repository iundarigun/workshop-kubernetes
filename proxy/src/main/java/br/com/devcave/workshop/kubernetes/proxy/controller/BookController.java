package br.com.devcave.workshop.kubernetes.proxy.controller;

import br.com.devcave.workshop.kubernetes.proxy.response.BookInternalResponse;
import br.com.devcave.workshop.kubernetes.proxy.service.IceAndFireService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {

    private IceAndFireService iceAndFireService;

    @GetMapping("{id}")
    public HttpEntity<BookInternalResponse> findById(@PathVariable Long id){
        log.info("M=findById, id={}", id);
        BookInternalResponse book = iceAndFireService.getBook(id);
        return ResponseEntity.ok(book);
    }
}
