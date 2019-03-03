package br.com.devcave.workshop.kubernetes.proxy.service;

import br.com.devcave.workshop.kubernetes.proxy.response.BookInternalResponse;
import br.com.devcave.workshop.kubernetes.proxy.response.CharacterInternalResponse;

public interface IceAndFireService {

    BookInternalResponse getBook(Long id);

    CharacterInternalResponse getCharacter(Long id);
}
