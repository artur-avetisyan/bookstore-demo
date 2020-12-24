package dev.avetisyan.egs.bookstore.repositories;

import dev.avetisyan.egs.bookstore.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}