package br.com.devcave.workshop.kubernetes.preferences.service;

import br.com.devcave.workshop.kubernetes.preferences.client.ProxyClient;
import br.com.devcave.workshop.kubernetes.preferences.domain.entity.Preference;
import br.com.devcave.workshop.kubernetes.preferences.domain.entity.TypePreference;
import br.com.devcave.workshop.kubernetes.preferences.domain.entity.User;
import br.com.devcave.workshop.kubernetes.preferences.domain.request.UserRequest;
import br.com.devcave.workshop.kubernetes.preferences.domain.response.BookResponse;
import br.com.devcave.workshop.kubernetes.preferences.domain.response.CharacterResponse;
import br.com.devcave.workshop.kubernetes.preferences.domain.response.UserResponse;
import br.com.devcave.workshop.kubernetes.preferences.mapper.UserMapper;
import br.com.devcave.workshop.kubernetes.preferences.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class UserServiceImpl implements UserService {

    private ProxyClient proxyClient;

    private UserRepository userRepository;

    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> search() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(u -> {
                    return buildUserResponse(u);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);

        return buildUserResponse(user);
    }

    @Override
    @Transactional
    public Long create(UserRequest userRequest) {
        Optional<User> user = userRepository.findByName(userRequest.getName());
        return user.orElse(userRepository.save(User.builder().name(userRequest.getName()).build())).getId();
    }

    @Override
    @Transactional
    public void addCharacter(Long id, Long characterId) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        CharacterResponse response = proxyClient.getCharacter(characterId);
        if (response == null){
            throw new RuntimeException();
        }
        addPreferences(user, characterId, TypePreference.CHARACTER);
    }

    @Override
    @Transactional
    public void addBook(Long id, Long characterId) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        BookResponse response = proxyClient.getBook(characterId);
        if (response == null){
            throw new RuntimeException();
        }
        addPreferences(user, characterId, TypePreference.BOOK);
    }

    private UserResponse buildUserResponse(User u) {
        UserResponse userResponse = userMapper.toUserResponse(u);
        userResponse.setBookList(
                u.getPreferenceList()
                        .stream()
                        .filter(p -> p.getType().equals(TypePreference.BOOK))
                        .map(p -> proxyClient.getBook(p.getExternalReference()))
                        .collect(Collectors.toList()));
        userResponse.setCharacterList(
                u.getPreferenceList()
                        .stream()
                        .filter(p -> p.getType().equals(TypePreference.CHARACTER))
                        .map(p -> proxyClient.getCharacter(p.getExternalReference()))
                        .collect(Collectors.toList()));
        return userResponse;
    }

    private void addPreferences(User user, Long externalId, TypePreference type) {
        Optional<Preference> preference = user.getPreferenceList()
                .stream()
                .filter(u -> u.getExternalReference().equals(externalId)
                        && u.getType().equals(type))
                .findAny();
        if (preference.isEmpty()){
            user.getPreferenceList()
                    .add(
                            Preference
                                    .builder()
                                    .type(type)
                                    .externalReference(externalId)
                                    .user(user)
                                    .build());
            userRepository.save(user);
        }
    }

}
