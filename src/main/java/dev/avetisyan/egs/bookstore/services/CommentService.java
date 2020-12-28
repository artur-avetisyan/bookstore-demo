package dev.avetisyan.egs.bookstore.services;

import dev.avetisyan.egs.bookstore.auth.User;
import dev.avetisyan.egs.bookstore.dtos.request.CommentRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.CommentResponseDto;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorCode;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorDto;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;
import dev.avetisyan.egs.bookstore.entities.CommentEntity;
import dev.avetisyan.egs.bookstore.repositories.CommentRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService extends BaseService implements ICommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, ModelMapper mapper) {
        super(mapper);
        this.commentRepository = commentRepository;
    }

    @Override
    public ResponseDto create(long bookId, CommentRequestDto body, long userId) {
        CommentEntity comment = mapper.map(body, CommentEntity.class);
        comment.setBookId(bookId);
        comment.setCommenterId(userId);
        comment.setCommentedAt(new Timestamp(System.currentTimeMillis()));

        return tryToSaveComment(comment);
    }

    @Override
    public ResponseDto update(long bookId, long commentId, CommentRequestDto body, long userId) {
        Optional<CommentEntity> result = commentRepository.findById(commentId);
        if (result.isEmpty()) return createNotFoundResponse();

        CommentEntity comment = result.get();
        if (comment.getBookId() != bookId) return createMismatchResponse();

        comment.setText(body.getText());
        return tryToSaveComment(comment);
    }

    @Override
    public ResponseDto getComments(long bookId, PageCriteria pageCriteria) {

        Pageable pageable = PageRequest.of(pageCriteria.getPageIndex(), pageCriteria.getPageSize(),
                Sort.by(Sort.Direction.ASC, "commentedAt", "id"));

        Page<CommentEntity> commentEntities = commentRepository.findAllByBookId(bookId, pageable);

        List<CommentResponseDto> comments = commentEntities.get().
                map(e -> mapper.map(e, CommentResponseDto.class)).
                collect(Collectors.toCollection(ArrayList::new));

        return ResponseDto.success((Serializable) comments, commentEntities.getTotalElements());
    }

    @Override
    public ResponseDto delete(long bookId, long commentId, User currentUser) {
        Optional<CommentEntity> result = commentRepository.findById(commentId);
        if (result.isEmpty()) return createNotFoundResponse();

        CommentEntity comment = result.get();
        if (comment.getBookId() != bookId) return createMismatchResponse();

        if (!currentUser.isAdmin() && currentUser.getUserId() != comment.getCommenterId()) {
            return ResponseDto.error(new ErrorDto(ErrorCode.ERR_AD,
                    "Non-admin user can delete only his/her own comments."));
        }

        commentRepository.delete(comment);
        return ResponseDto.success(true);
    }

    private static ResponseDto createNotFoundResponse() {
        ErrorDto error = new ErrorDto(ErrorCode.ERR_NF, "Comment with the specified id not found");
        return ResponseDto.error(error);
    }

    private static ResponseDto createMismatchResponse() {
        ErrorDto error = new ErrorDto(ErrorCode.ERR_DM,
                "The book with the specified id has no comment with the provided id on it");
        return ResponseDto.error(error);
    }

    private ResponseDto tryToSaveComment(CommentEntity comment) {
        try {
            CommentEntity saved = commentRepository.save(comment);
            return ResponseDto.success(mapper.map(saved, CommentResponseDto.class));

        } catch (DataIntegrityViolationException e) {
            ErrorDto error = null;
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
                if ("fk_books_book_id_2_books_id".equals(cause.getConstraintName())) {
                    error = new ErrorDto(ErrorCode.ERR_NF, "A book with the specified id not found.");
                } else if ("fk_comments_commenter_id_2_users_id".equals(cause.getConstraintName())) {
                    error = new ErrorDto(ErrorCode.ERR_NF, "Commenter user with the specified id not found.");
                }
            } else if (e.getCause() instanceof DataException && e.getCause().getCause() != null) {
                error = new ErrorDto(ErrorCode.ERR_LE, e.getCause().getCause().getLocalizedMessage());
            }

            return ResponseDto.error(error == null ? ErrorDto.internalError() : error);
        }
    }
}
