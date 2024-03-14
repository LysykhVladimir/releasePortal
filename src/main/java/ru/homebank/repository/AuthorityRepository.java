package ru.homebank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.homebank.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
