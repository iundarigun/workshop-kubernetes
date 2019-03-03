package br.com.devcave.workshop.kubernetes.preferences.mapper;

import br.com.devcave.workshop.kubernetes.preferences.domain.entity.User;
import br.com.devcave.workshop.kubernetes.preferences.domain.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(target="bookList", ignore = true),
            @Mapping(target="characterList", ignore = true)
    })
    UserResponse toUserResponse(User user);
}
