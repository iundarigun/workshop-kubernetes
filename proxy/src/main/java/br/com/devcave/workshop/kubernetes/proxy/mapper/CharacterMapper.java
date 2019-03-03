package br.com.devcave.workshop.kubernetes.proxy.mapper;

import br.com.devcave.workshop.kubernetes.proxy.response.CharacterInternalResponse;
import br.com.devcave.workshop.kubernetes.proxy.response.CharacterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CharacterMapper {

    @Mappings({
            @Mapping(target = "houseList", ignore = true)
    })
    CharacterInternalResponse toCharacterInternalResponse(CharacterResponse characterResponse);

}
