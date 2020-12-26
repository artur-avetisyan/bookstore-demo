package dev.avetisyan.egs.bookstore.controllers;

import dev.avetisyan.egs.bookstore.dtos.request.BookRequestDto;
import dev.avetisyan.egs.bookstore.dtos.request.filters.BookFilter;
import dev.avetisyan.egs.bookstore.dtos.request.general.PageCriteria;
import dev.avetisyan.egs.bookstore.dtos.request.general.SortCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorCode;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;
import dev.avetisyan.egs.bookstore.services.IBookService;
import dev.avetisyan.egs.bookstore.services.ICommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/books")
@Api("API for managing books")
public class BookController extends BaseController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Stream.of("name", "publishDate").collect(Collectors.toUnmodifiableSet());

    private final IBookService bookService;

    @Autowired
    public BookController(IBookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ApiOperation(value = "Create a book",
            notes = "Admin users create approved books, while the other users " +
                    "have to wait for admin to approve the new book.")
    public ResponseEntity<ResponseDto> createBook(@Valid @RequestBody BookRequestDto body,
                                                  BindingResult bindingResult) {

        ResponseEntity<ResponseDto> bindingErrors = getBindingErrorsIfExist(
                bindingResult, Map.of("name", ErrorCode.ERR_LE, "description", ErrorCode.ERR_LE));
        if (bindingErrors != null) return bindingErrors;

        // FIXME: get user from session
        boolean isAdmin = true;
        long userId = 1;
        ResponseDto result = bookService.create(body, userId, isAdmin);

        return generateCreatedResponse(result);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update a book", notes = "Admins can update every book, users can update only their own books.")
    public ResponseEntity<ResponseDto> updateBook(@Valid @RequestBody BookRequestDto body,
                                                  BindingResult bindingResult, @PathVariable long id) {

        ResponseEntity<ResponseDto> bindingErrors = getBindingErrorsIfExist(
                bindingResult, Map.of("name", ErrorCode.ERR_LE, "description", ErrorCode.ERR_LE));
        if (bindingErrors != null) return bindingErrors;

        // FIXME: get user from the session
        boolean isAdmin = true;
        long userId = 1;
        // TODO: pass the current user instead of 2 params?
        ResponseDto result = bookService.update(id, body, userId, isAdmin);

        return generateResponse(result);
    }

    // FIXME: add authorization
    @PatchMapping("/{id}/approval")
    @ApiOperation(value = "Approve a book",
            notes = "Only admin users have privilege to approve a book. Though not necessary, " +
                    "this endpoint can also be used to revoke approval from a book.")
    public ResponseEntity<ResponseDto> setApproved(@PathVariable long id, @RequestBody boolean isApproved) {
        ResponseDto result = bookService.setApproved(id, isApproved);
        return generateResponse(result);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get a book by id", notes = "Non-admin users can get only approved " +
            "books and non-approved books created by themselves, whereas admins can get all books.")
    @ApiResponses(@ApiResponse(code = 403, message = "Non-admin users get this response when the " +
            "requested book has not been approved by admins yet, and they are not the creators of the book."))
    public ResponseEntity<ResponseDto> getBookById(@PathVariable long id) {

        // FIXME: get the current user from the session
        boolean isAdmin = true;
        long userId = 1;
        // TODO: pass the current user instead of 2 params?
        ResponseDto result = bookService.findById(id, userId, isAdmin);

        return generateResponse(result);
    }

    @GetMapping
    @ApiOperation(value = "Get all books", notes = "Non-admin users can see only approved books and the books " +
            "created by themselves, whereas admins can see all books. Filtering with the book name, description, " +
            "author, creator and approved status is available. This endpoint provides sorting and pagination.")
    public ResponseEntity<ResponseDto> getBooks(
            BookFilter filter, PageCriteria pageCriteria, SortCriteria sortCriteria) {

        ResponseEntity<ResponseDto> sortingError = getSortingFieldErrorIfExists(sortCriteria, ALLOWED_SORT_FIELDS);
        if (sortingError != null) return sortingError;

        // FIXME: get the user from the current session
        boolean isAdmin = true;
        long userId = 1;
        // TODO: pass the current user instead of 2 params?
        ResponseDto result = bookService.getBooks(filter, isAdmin, userId, pageCriteria, sortCriteria);

        return generateResponse(result);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a book", notes = "Non-admin users can only delete " +
            "the books created by themselves, whereas admins can delete all books.")
    public ResponseEntity<ResponseDto> deleteBook(@PathVariable long id) {
        // FIXME: get the user from the current session
        boolean isAdmin = true;
        long userId = 1;
        // TODO: pass the current user instead of 2 params?
        ResponseDto result = bookService.delete(id, userId, isAdmin);

        return generateResponse(result);
    }
}
