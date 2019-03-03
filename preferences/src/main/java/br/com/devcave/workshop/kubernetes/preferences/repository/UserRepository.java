package br.com.devcave.workshop.kubernetes.preferences.repository;

import br.com.devcave.workshop.kubernetes.preferences.domain.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();

    Optional<User> findByName(String name);
}
