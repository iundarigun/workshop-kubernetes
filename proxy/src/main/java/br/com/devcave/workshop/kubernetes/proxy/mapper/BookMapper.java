package br.com.devcave.workshop.kubernetes.proxy.mapper;

import br.com.devcave.workshop.kubernetes.proxy.response.BookInternalResponse;
import br.com.devcave.workshop.kubernetes.proxy.response.BookResponse;
import br.com.devcave.workshop.kubernetes.proxy.response.CharacterInternalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mappings({
            @Mapping(target="name", source = "book.name"),
            @Mapping(target="released", source = "book.released"),
            @Mapping(target="authors", source = "book.authors"),
            @Mapping(target="characterList", source = "characterList")
    })
    BookInternalResponse toBookInternalResponse(BookResponse book, List<CharacterInternalResponse> characterList);
}
