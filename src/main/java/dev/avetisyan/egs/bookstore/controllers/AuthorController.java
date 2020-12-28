package dev.avetisyan.egs.bookstore.controllers;

import dev.avetisyan.egs.bookstore.auth.User;
import dev.avetisyan.egs.bookstore.dtos.request.AuthorRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.filters.AuthorFilter;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.request.general.SortCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorCode;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;
import dev.avetisyan.egs.bookstore.services.IAuthorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/authors")
@Api("API for managing authors of the books")
public class AuthorController extends BaseController {

    // In production code I'll consider using reflection to create
    // allowed sort field sets at the initialization stage.
    // Lets keep it simple for demo.
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Stream.of("fullName", "birthDate").collect(Collectors.toUnmodifiableSet());

    private final IAuthorService authorService;

    @Autowired
    public AuthorController(IAuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    @ApiOperation(value = "Create an author",
            notes = "Admin users create approved authors, while the other users " +
                    "have to wait for admin to approve the new author.")
    public ResponseEntity<ResponseDto> createAuthor(@Valid @RequestBody AuthorRequestDto body,
                                                    BindingResult bindingResult,
                                                    @ApiIgnore @AuthenticationPrincipal User currentUser) {

        ResponseEntity<ResponseDto> bindingError = getBindingErrorsIfExist(bindingResult,
                Collections.singletonMap("fullName", ErrorCode.ERR_LE));
        if (bindingError != null) return bindingError;

        ResponseDto result = authorService.create(body, currentUser.isAdmin());
        return generateCreatedResponse(result);
    }

    // <TODO>: only admins can update author, add authorization
    @PutMapping("/{id}")
    @ApiOperation(value = "Update an author",
            notes = "Only admin users have privilege to update an author.")
    public ResponseEntity<ResponseDto> updateAuthor(@Valid @RequestBody AuthorRequestDto body,
                                                    BindingResult bindingResult, @PathVariable int id) {

        ResponseEntity<ResponseDto> bindingError = getBindingErrorsIfExist(bindingResult,
                Collections.singletonMap("fullName", ErrorCode.ERR_LE));
        if (bindingError != null) return bindingError;

        ResponseDto result = authorService.update(id, body);
        return generateResponse(result);
    }

    // TODO: only admins can approve an author, add authorization
    @PatchMapping("/{id}/approval")
    @ApiOperation(value = "Approve an author",
            notes = "Only admin users have privilege to approve an author. Though not necessary, " +
                    "this endpoint can also be used to revoke approval from author.")
    public ResponseEntity<ResponseDto> setApproved(@PathVariable int id, @RequestBody boolean isApproved) {
        ResponseDto result = authorService.setApproved(id, isApproved);
        return generateResponse(result);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get an author by id", notes = "Non-admin users can get " +
            "only approved authors, whereas admins can get all authors.")
    @ApiResponses(@ApiResponse(code = 403, message = "Non-admin users get this response " +
            "when the requested author has not been approved by admins yet, as they can " +
            "get only approved authors."))
    public ResponseEntity<ResponseDto> getAuthorById(@PathVariable int id,
                                                     @ApiIgnore @AuthenticationPrincipal User currentUser) {

        ResponseDto result = authorService.findById(id, currentUser.isAdmin());
        return generateResponse(result);
    }

    @GetMapping
    @ApiOperation(value = "Get all authors", notes = "Non-admin users can see only " +
            "approved authors, whereas admins can see all authors. Filtering with name and " +
            "approved status is available. This endpoint provides sorting and pagination.")
    public ResponseEntity<ResponseDto> getAuthors(@ApiIgnore @AuthenticationPrincipal User currentUser,
            AuthorFilter filter, PageCriteria pageCriteria, SortCriteria sortCriteria) {

        ResponseEntity<ResponseDto> sortingError = getSortingFieldErrorIfExists(sortCriteria, ALLOWED_SORT_FIELDS);
        if (sortingError != null) return sortingError;

        ResponseDto result = authorService.getAuthors(filter, currentUser.isAdmin(), pageCriteria, sortCriteria);
        return generateResponse(result);
    }
}
