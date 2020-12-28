package dev.avetisyan.egs.bookstore.services;

import dev.avetisyan.egs.bookstore.auth.User;
import dev.avetisyan.egs.bookstore.dtos.request.CommentRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;

public interface ICommentService {
    ResponseDto create(long bookId, CommentRequestDto body, long userId);

    ResponseDto update(long bookId, long commentId, CommentRequestDto body, long userId);

    ResponseDto getComments(long bookId, PageCriteria pageCriteria);

    ResponseDto delete(long bookId, long commentId, User currentUser);
}
