package br.com.devcave.workshop.kubernetes.proxy.controller;

import br.com.devcave.workshop.kubernetes.proxy.response.BookInternalResponse;
import br.com.devcave.workshop.kubernetes.proxy.service.IceAndFireService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {

    private IceAndFireService iceAndFireService;

    @GetMapping("{id}")
    public HttpEntity<BookInternalResponse> findById(@PathVariable Long id){
        BookInternalResponse book = iceAndFireService.getBook(id);
        return ResponseEntity.ok(book);
    }
}
