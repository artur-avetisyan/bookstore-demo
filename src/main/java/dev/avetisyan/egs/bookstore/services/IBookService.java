package dev.avetisyan.egs.bookstore.services;

import dev.avetisyan.egs.bookstore.auth.User;
import dev.avetisyan.egs.bookstore.dtos.request.BookRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.filters.BookFilter;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.request.general.SortCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;

public interface IBookService {
    ResponseDto create(BookRequestDto dto, User currentUser);

    ResponseDto update(long id, BookRequestDto dto, User currentUser);

    ResponseDto setApproved(long bookId, boolean isApproved);

    ResponseDto findById(long id, User currentUser);

    ResponseDto getBooks(BookFilter filter, User currentUser,
                         PageCriteria pageCriteria, SortCriteria sortCriteria);

    ResponseDto delete(long bookId, User currentUser);
}
