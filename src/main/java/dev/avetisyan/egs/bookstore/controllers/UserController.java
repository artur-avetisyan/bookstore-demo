package dev.avetisyan.egs.bookstore.controllers;

import dev.avetisyan.egs.bookstore.dtos.request.UserRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorCode;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;
import dev.avetisyan.egs.bookstore.services.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Api("API for managing users")
public class UserController extends BaseController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ApiOperation(value = "Create a user", notes = "User with <User> role is created.")
    public ResponseEntity<ResponseDto> createAuthor(@Valid @RequestBody UserRequestDto body,
                                                    BindingResult bindingResult) {

        ResponseEntity<ResponseDto> bindingError = getBindingErrorsIfExist(bindingResult,
                Map.of("username", ErrorCode.ERR_LE, "email", ErrorCode.ERR_LE));
        if (bindingError != null) return bindingError;

        ResponseDto result = userService.create(body);
        return generateCreatedResponse(result);
    }

    @GetMapping
    @ApiOperation(value = "Get users", notes = "Admins can get all users with pagination and filtered by role.")
    public ResponseEntity<ResponseDto> getUsers(@RequestParam(required = false) Short roleId,
                                                PageCriteria pageCriteria) {

        ResponseDto result = userService.getUsers(roleId, pageCriteria);
        return generateResponse(result);
    }

    // In production code there can also be endpoints for user update and deletion logic,
    // password reset, more convenient getters with filtering and sorting.
}
