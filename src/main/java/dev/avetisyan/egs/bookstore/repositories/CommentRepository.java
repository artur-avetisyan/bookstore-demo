package dev.avetisyan.egs.bookstore.repositories;

import dev.avetisyan.egs.bookstore.entities.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Page<CommentEntity> findAllByBookId(long bookId, Pageable pageable);
}
