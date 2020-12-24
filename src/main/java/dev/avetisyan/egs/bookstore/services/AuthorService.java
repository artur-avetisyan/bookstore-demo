package dev.avetisyan.egs.bookstore.services;

import dev.avetisyan.egs.bookstore.dtos.request.AuthorRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.filters.AuthorFilter;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.request.general.SortCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.AuthorResponseDto;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorCode;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorDto;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;
import dev.avetisyan.egs.bookstore.entities.AuthorEntity;
import dev.avetisyan.egs.bookstore.repositories.AuthorRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.modelmapper.ModelMapper;
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
public class AuthorService extends BaseService implements IAuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, ModelMapper mapper) {
        super(mapper);
        this.authorRepository = authorRepository;
    }

    @Override
    public ResponseDto create(AuthorRequestDto dto, boolean isApproved) {
        AuthorEntity author = mapper.map(dto, AuthorEntity.class);
        author.setIsApproved(isApproved);

        return tryToSaveAuthor(author);
    }

    private ResponseDto tryToSaveAuthor(AuthorEntity author) {
        try {
            AuthorEntity saved = authorRepository.save(author);
            return ResponseDto.success(mapper.map(saved, AuthorResponseDto.class));

        } catch (DataIntegrityViolationException e) {

            ErrorDto error = null;
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
                if ("uni_idx_author_full_name_bd".equals(cause.getConstraintName()) ||
                        "uni_idx_author_full_name".equals(cause.getConstraintName())) {
                    error = new ErrorDto(ErrorCode.ERR_DUP,
                            "Author already exists. If not listed in the authors " +
                                    "list yet, please wait until admins approve it.");
                }
            } else if (e.getCause() instanceof DataException && e.getCause().getCause() != null) {
                error = new ErrorDto(ErrorCode.ERR_LE, e.getCause().getCause().getLocalizedMessage());
            }

            return ResponseDto.error(error == null ? ErrorDto.internalError() : error);
        }
    }

    @Override
    public ResponseDto update(int authorId, AuthorRequestDto dto) {
        Optional<AuthorEntity> result = authorRepository.findById(authorId);

        if (result.isEmpty())
            return createNotFoundResponse();

        AuthorEntity author = result.get();
        author.setFullName(dto.getFullName());
        author.setBirthDate(dto.getBirthDate());

        return tryToSaveAuthor(author);
    }

    private static ResponseDto createNotFoundResponse() {
        ErrorDto error = new ErrorDto(ErrorCode.ERR_NF, "An author with the specified id not found");
        return ResponseDto.error(error);
    }

    @Override
    public ResponseDto setApproved(int authorId, boolean isApproved) {
        Optional<AuthorEntity> result = authorRepository.findById(authorId);

        if (result.isEmpty())
            return createNotFoundResponse();

        AuthorEntity author = result.get();
        if (isApproved != author.getIsApproved()) {
            author.setIsApproved(isApproved);
            authorRepository.save(author);
        }

        return ResponseDto.success(isApproved);
    }


    @Override
    public ResponseDto findById(int id, boolean isAdmin) {
        Optional<AuthorEntity> result = authorRepository.findById(id);

        if (result.isEmpty()) return createNotFoundResponse();

        AuthorEntity author = result.get();
        if (!isAdmin && !author.getIsApproved()) {
            return ResponseDto.error(new ErrorDto(ErrorCode.ERR_AD,
                    "Non-admin user can get only approved authors."));
        }

        return ResponseDto.success(mapper.map(author, AuthorResponseDto.class));
    }

    @Override
    public ResponseDto getAuthors(AuthorFilter filter,
                                  boolean isAdmin, PageCriteria pageCriteria, SortCriteria sortCriteria) {

        if (!isAdmin && filter.getApproved() != null && filter.getApproved()) {
            return ResponseDto.error(new ErrorDto(ErrorCode.ERR_AD,
                    "Only admin users can see not approved authors."));
        }

        if (sortCriteria.getSortField() == null || sortCriteria.getSortField().isEmpty()) {
            sortCriteria.setSortField("fullName");
        }

        Pageable pageable = PageRequest.of(pageCriteria.getPageIndex(), pageCriteria.getPageSize(),
                Sort.by(sortCriteria.getSortDir(), sortCriteria.getSortField(), "id"));

        AuthorEntity probe = new AuthorEntity();
        probe.setFullName(filter.getFullName());
        probe.setIsApproved(filter.getApproved());

        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("isApproved", ExampleMatcher.GenericPropertyMatcher::exact).
                withMatcher("fullName", ExampleMatcher.GenericPropertyMatcher::contains).
                withIgnoreCase().
                withIgnoreNullValues().
                withIgnorePaths("id", "birthDate");

        Page<AuthorEntity> authorEntities =
                authorRepository.findAll(Example.of(probe, matcher), pageable);

        List<AuthorResponseDto> authors = authorEntities.get().
                map(e -> mapper.map(e, AuthorResponseDto.class)).
                collect(Collectors.toCollection(ArrayList::new));

        return ResponseDto.success((Serializable) authors, authorEntities.getTotalElements());
    }
}
