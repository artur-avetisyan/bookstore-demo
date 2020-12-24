package dev.avetisyan.egs.bookstore.services;

import dev.avetisyan.egs.bookstore.dtos.request.AuthorRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.filters.AuthorFilter;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.request.general.SortCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;

public interface IAuthorService {

    ResponseDto create(AuthorRequestDto dto, boolean isApproved);

    ResponseDto update(int authorId, AuthorRequestDto dto);

    ResponseDto setApproved(int authorId, boolean isApproved);

    ResponseDto findById(int id, boolean isAdmin);

    ResponseDto getAuthors(AuthorFilter filter, boolean isAdmin,
                           PageCriteria pageCriteria, SortCriteria sortCriteria);
}
