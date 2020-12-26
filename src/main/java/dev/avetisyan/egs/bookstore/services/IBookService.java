package dev.avetisyan.egs.bookstore.services;

import dev.avetisyan.egs.bookstore.dtos.request.BookRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.filters.BookFilter;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.request.general.SortCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;

public interface IBookService {
    ResponseDto create(BookRequestDto dto, long creatorId, boolean isApproved);

    ResponseDto update(long id, BookRequestDto dto, long currentUserId, boolean isAdmin);

    ResponseDto setApproved(long bookId, boolean isApproved);

    ResponseDto findById(long id, long currentUserId, boolean isAdmin);

    ResponseDto getBooks(BookFilter filter, boolean isAdmin, long userId,
                         PageCriteria pageCriteria, SortCriteria sortCriteria);

    ResponseDto delete(long bookId, long userId, boolean isAdmin);
}
