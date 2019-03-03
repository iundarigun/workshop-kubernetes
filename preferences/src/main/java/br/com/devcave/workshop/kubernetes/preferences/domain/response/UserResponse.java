package br.com.devcave.workshop.kubernetes.preferences.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class UserResponse {

    private Long id;

    private String name;

    private List<BookResponse> bookList;

    private List<CharacterResponse> characterList;
}
