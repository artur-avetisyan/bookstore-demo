package dev.avetisyan.egs.bookstore.services;

import dev.avetisyan.egs.bookstore.auth.User;
import dev.avetisyan.egs.bookstore.dtos.request.BookRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.filters.BookFilter;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.request.general.SortCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.AuthorResponseDto;
import dev.avetisyan.egs.bookstore.dtos.response.BookResponseDto;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorCode;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorDto;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;
import dev.avetisyan.egs.bookstore.entities.BookEntity;
import dev.avetisyan.egs.bookstore.repositories.BookRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService extends BaseService implements IBookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository, ModelMapper mapper) {
        super(mapper);
        this.bookRepository = bookRepository;
    }

    @Override
    public ResponseDto create(BookRequestDto dto, User currentUser) {
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BookEntity book = mapper.map(dto, BookEntity.class);
        book.setCreatorId(currentUser.getUserId());
        book.setIsApproved(currentUser.isAdmin());

        return tryToSaveBook(book);
    }

    @Override
    public ResponseDto update(long id, BookRequestDto dto, User currentUser) {
        Optional<BookEntity> result = bookRepository.findById(id);

        if (result.isEmpty()) {
            return createNotFoundResponse();
        }

        BookEntity book = result.get();
        if (!currentUser.isAdmin() && book.getCreatorId() != currentUser.getUserId()) {
            return ResponseDto.error(new ErrorDto(ErrorCode.ERR_AD,
                    "Non-admin users can update only books created by themselves."));
        }

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.typeMap(BookRequestDto.class, BookEntity.class).map(dto, book);

        return tryToSaveBook(book);
    }

    @Override
    public ResponseDto setApproved(long bookId, boolean isApproved) {
        Optional<BookEntity> result = bookRepository.findById(bookId);

        if (result.isEmpty()) return createNotFoundResponse();

        BookEntity book = result.get();
        if (book.getIsApproved() != isApproved) {
            book.setIsApproved(isApproved);
            bookRepository.save(book);
        }

        return ResponseDto.success(isApproved);
    }

    @Override
    public ResponseDto findById(long id, User currentUser) {
        Optional<BookEntity> result = bookRepository.findById(id);

        if (result.isEmpty() || result.get().isDeleted()) return createNotFoundResponse();

        BookEntity book = result.get();
        if (!currentUser.isAdmin() && !book.getIsApproved() && book.getCreatorId() != currentUser.getUserId()) {
            return ResponseDto.error(new ErrorDto(ErrorCode.ERR_AD, "Non-admin user can't get the books " +
                    "created by the other users that have not been approved yet."));
        }

        return ResponseDto.success(mapper.map(book, AuthorResponseDto.class));
    }

    @Override
    public ResponseDto getBooks(BookFilter filter, User currentUser,
                                PageCriteria pageCriteria, SortCriteria sortCriteria) {

        if (!currentUser.isAdmin() && filter.getCreatorId() != currentUser.getUserId() &&
                filter.getApproved() != null && filter.getApproved()) {
            return ResponseDto.error(new ErrorDto(ErrorCode.ERR_AD, "Only admin users " +
                    "can get not approved books created by other users."));
        }

        if (filter.getCreatorId() == null && !currentUser.isAdmin()) filter.setCreatorId(currentUser.getUserId());

        if (sortCriteria.getSortField() == null || sortCriteria.getSortField().isEmpty()) {
            sortCriteria.setSortField("name");
        }

        Pageable pageable = PageRequest.of(pageCriteria.getPageIndex(), pageCriteria.getPageSize(),
                Sort.by(sortCriteria.getSortDir(), sortCriteria.getSortField(), "id"));

        BookEntity probe = new BookEntity();
        probe.setName(filter.getName());
        probe.setDescription(filter.getDescription());
        probe.setAuthorId(filter.getAuthorId());
        probe.setIsApproved(filter.getApproved());
        probe.setCreatorId(filter.getCreatorId());
        probe.setDeleted(false);

        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("isApproved", ExampleMatcher.GenericPropertyMatcher::exact).
                withMatcher("authorId", ExampleMatcher.GenericPropertyMatcher::exact).
                withMatcher("creatorId", ExampleMatcher.GenericPropertyMatcher::exact).
                withMatcher("name", ExampleMatcher.GenericPropertyMatcher::contains).
                withMatcher("description", ExampleMatcher.GenericPropertyMatcher::contains).
                withIgnoreCase().
                withIgnoreNullValues().
                withIgnorePaths("id", "publishDate", "price");

        Page<BookEntity> bookEntities = bookRepository.findAll(Example.of(probe, matcher), pageable);

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<BookResponseDto> books = bookEntities.get().map(e -> mapper.map(e, BookResponseDto.class)).
                collect(Collectors.toCollection(ArrayList::new));

        return ResponseDto.success(((Serializable) books), bookEntities.getTotalElements());
    }

    @Override
    public ResponseDto delete(long bookId, User currentUser) {
        Optional<BookEntity> result = bookRepository.findById(bookId);

        if (result.isEmpty()) return createNotFoundResponse();

        BookEntity book = result.get();
        if (!currentUser.isAdmin() && book.getCreatorId() != currentUser.getUserId()) {
            return ResponseDto.error(new ErrorDto(ErrorCode.ERR_AD, "Only admin users " +
                    "can delete the books created by the other users."));
        }

        if (!book.isDeleted()) {
            book.setDeleted(true);
            bookRepository.save(book);
        }

        return ResponseDto.success(true);
    }

    private static ResponseDto createNotFoundResponse() {
        ErrorDto error = new ErrorDto(ErrorCode.ERR_NF, "A book with the specified id not found");
        return ResponseDto.error(error);
    }

    private ResponseDto tryToSaveBook(BookEntity book) {
        try {
            BookEntity saved = bookRepository.save(book);
            mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return ResponseDto.success(mapper.map(saved, BookResponseDto.class));

        } catch (DataIntegrityViolationException e) {
            ErrorDto error = null;
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
                error = switch (cause.getConstraintName()) {
                    case "fk_books_author_id_2_authors_id" -> new ErrorDto(ErrorCode.ERR_NF, "An author with the specified id not found.");
                    case "fk_books_creator_id_2_users_id" -> new ErrorDto(ErrorCode.ERR_NF, "A creator user with the specified id not found.");
                    case "uni_idx_book_name_pub_dt_ar_id_cr_id", "uni_idx_book_name_pub_dt_cr_id" -> new ErrorDto(ErrorCode.ERR_DUP, "The book already exists. If not shown in the " +
                            "list, please wait until admins approve it.");
                    default -> null;
                };
            } else if (e.getCause() instanceof DataException && e.getCause().getCause() != null) {
                error = new ErrorDto(ErrorCode.ERR_LE, e.getCause().getCause().getLocalizedMessage());
            }

            return ResponseDto.error(error == null ? ErrorDto.internalError() : error);
        }
    }
}
