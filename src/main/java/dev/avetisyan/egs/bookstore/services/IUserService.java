package dev.avetisyan.egs.bookstore.services;

import dev.avetisyan.egs.bookstore.dtos.request.UserRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;

public interface IUserService {
    ResponseDto create(UserRequestDto body);

    ResponseDto getUsers(Short roleId, PageCriteria pageCriteria);
}
