package dev.avetisyan.egs.bookstore.repositories;

import dev.avetisyan.egs.bookstore.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
}
