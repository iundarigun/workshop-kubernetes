package br.com.devcave.workshop.kubernetes.preferences.service;

import br.com.devcave.workshop.kubernetes.preferences.domain.request.UserRequest;
import br.com.devcave.workshop.kubernetes.preferences.domain.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> search();

    UserResponse findById(Long id);

    Long create(UserRequest userRequest);

    void addCharacter(Long id, Long characterId);

    void addBook(Long id, Long characterId);
}
