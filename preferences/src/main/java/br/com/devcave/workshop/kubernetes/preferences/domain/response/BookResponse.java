package br.com.devcave.workshop.kubernetes.preferences.domain.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookResponse {

    private String name;

    private LocalDateTime released;

    private List<String> authors;

    private List<CharacterResponse> characterList;
}
