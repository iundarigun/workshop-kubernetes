package br.com.devcave.workshop.kubernetes.proxy.service;

import br.com.devcave.workshop.kubernetes.proxy.response.BookInternalResponse;

public interface IceAndFireService {

    BookInternalResponse getBook(Long id);
}
