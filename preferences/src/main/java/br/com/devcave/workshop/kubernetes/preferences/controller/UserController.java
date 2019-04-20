package br.com.devcave.workshop.kubernetes.preferences.controller;

import br.com.devcave.workshop.kubernetes.preferences.domain.request.UserRequest;
import br.com.devcave.workshop.kubernetes.preferences.domain.response.UserResponse;
import br.com.devcave.workshop.kubernetes.preferences.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("users")
public class UserController {

    private UserService userService;

    @GetMapping
    public HttpEntity<List<UserResponse>> search() {
        log.info("M=search");
        List<UserResponse> userList = userService.search();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("{id}")
    public HttpEntity<UserResponse> findById(@PathVariable Long id) {
        log.info("M=findById, id={}", id);
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public HttpEntity<?> create(@RequestBody UserRequest userRequest) {
        log.info("M=create, userRequest={}", userRequest);
        Long userId = userService.create(userRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .build(userId);

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}/character/{characterId}")
    public HttpEntity<?> addCharacter(@PathVariable Long id, @PathVariable Long characterId) {
        log.info("M=addCharacter, id={}, characterId={}", id, characterId);
        userService.addCharacter(id, characterId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/book/{bookId}")
    public HttpEntity<?> addBook(@PathVariable Long id, @PathVariable Long bookId) {
        log.info("M=addBook, id={}, bookId={}", id, bookId);
        userService.addBook(id, bookId);

        return ResponseEntity.noContent().build();
    }
}
