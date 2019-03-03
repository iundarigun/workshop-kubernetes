package br.com.devcave.workshop.kubernetes.proxy.controller;

import br.com.devcave.workshop.kubernetes.proxy.response.CharacterInternalResponse;
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
@RequestMapping("/characters")
public class CharacterController {

    private IceAndFireService iceAndFireService;

    @GetMapping("{id}")
    public HttpEntity<CharacterInternalResponse> findById(@PathVariable Long id){
        CharacterInternalResponse character = iceAndFireService.getCharacter(id);
        return ResponseEntity.ok(character);
    }
}
