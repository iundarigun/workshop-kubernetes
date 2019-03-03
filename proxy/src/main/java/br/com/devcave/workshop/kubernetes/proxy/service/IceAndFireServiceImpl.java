package br.com.devcave.workshop.kubernetes.proxy.service;

import br.com.devcave.workshop.kubernetes.proxy.client.IceAndFireClient;
import br.com.devcave.workshop.kubernetes.proxy.mapper.BookMapper;
import br.com.devcave.workshop.kubernetes.proxy.mapper.CharacterMapper;
import br.com.devcave.workshop.kubernetes.proxy.response.BookInternalResponse;
import br.com.devcave.workshop.kubernetes.proxy.response.BookResponse;
import br.com.devcave.workshop.kubernetes.proxy.response.CharacterInternalResponse;
import br.com.devcave.workshop.kubernetes.proxy.response.CharacterResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class IceAndFireServiceImpl implements IceAndFireService {

    private IceAndFireClient iceAndFireClient;

    private BookMapper bookMapper;

    private CharacterMapper characterMapper;

    @Override
    public BookInternalResponse getBook(Long id) {
        BookResponse book = iceAndFireClient.getBook(id);

        List<CharacterResponse> characterList = book.getPovCharacters()
                .stream()
                .map(c ->  iceAndFireClient.getCharacter(getIdFromUrl(c)))
                .collect(Collectors.toList());

        return bookMapper.toBookInternalResponse(book, toCharacterInternalResponseList(characterList));
    }

    private List<CharacterInternalResponse> toCharacterInternalResponseList(
            List<CharacterResponse> characterResponseList) {
        return characterResponseList.stream()
                .map(c ->
                        {
                            CharacterInternalResponse characterInternalResponse = characterMapper.toCharacterInternalResponse(c);
                            characterInternalResponse.setHouseList(c.getAllegiances()
                                    .stream()
                                    .map(a-> iceAndFireClient.getHouse(getIdFromUrl(a)))
                                    .collect(Collectors.toList()));
                            return characterInternalResponse;
                        }
                )
                .collect(Collectors.toList());
    }


    private Long getIdFromUrl(String url) {
        String[] split = url.split("/");
        return Long.valueOf(split[split.length - 1]);
    }
}
