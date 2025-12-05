package utb.fai.soapservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import utb.fai.soapservice.Model.AuthorPersistent;
import utb.fai.soapservice.Model.BookPersistent;

import com.example.book_web_service.Author;
import com.example.book_web_service.Book;

import com.example.book_web_service.GetBookRequest;
import com.example.book_web_service.GetBookResponse;

import com.example.book_web_service.CreateBookRequest;
import com.example.book_web_service.CreateBookResponse;
import com.example.book_web_service.UpdateBookRequest;
import com.example.book_web_service.UpdateBookResponse;
import com.example.book_web_service.DeleteBookRequest;
import com.example.book_web_service.DeleteBookResponse;

import com.example.book_web_service.GetAuthorRequest;
import com.example.book_web_service.GetAuthorResponse;
import com.example.book_web_service.CreateAuthorRequest;
import com.example.book_web_service.CreateAuthorResponse;
import com.example.book_web_service.DeleteAuthorRequest;
import com.example.book_web_service.DeleteAuthorResponse;

@Endpoint
public class LibraryEndpoint {
    private static final String NAMESPACE_URI = "http://example.com/book-web-service";

    @Autowired
    private LibraryService libraryService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBookRequest")
    @ResponsePayload
    public GetBookResponse getBook(@RequestPayload GetBookRequest request) {
        var bookPersistent = libraryService.getBook(request.getBookId());

        if (bookPersistent == null) {
            throw new IllegalArgumentException("Book not found with id " + request.getBookId());
        }

        var response = new GetBookResponse();
        response.setBook(mapToJaxbBook(bookPersistent));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createBookRequest")
    @ResponsePayload
    public CreateBookResponse createBook(@RequestPayload CreateBookRequest request) {
        var book = request.getBook();

        if (book == null) {
            throw new IllegalArgumentException("Book must not be null");
        }

        validateNonEmpty(book.getTitle(), "title");

        var bookPersistent = new BookPersistent();
        bookPersistent.setTitle(book.getTitle());

        var authorPersistent = libraryService.getAuthor(book.getAuthorID());
        if (authorPersistent == null) {
            throw new IllegalArgumentException("Author not found with id " + book.getAuthorID());
        }
        bookPersistent.setAuthor(authorPersistent);

        var saved = libraryService.createBook(bookPersistent);

        var response = new CreateBookResponse();
        response.setBook(mapToJaxbBook(saved));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateBookRequest")
    @ResponsePayload
    public UpdateBookResponse updateBook(@RequestPayload UpdateBookRequest request) {
        var book = request.getBook();

        if (book == null) {
            throw new IllegalArgumentException("Book must not be null");
        }

        validateNonEmpty(book.getTitle(), "title");

        var bookPersistent = new BookPersistent();
        bookPersistent.setTitle(book.getTitle());

        if (book.getAuthorID() != 0) {
            var authorPersistent = libraryService.getAuthor(book.getAuthorID());
            if (authorPersistent == null) {
                throw new IllegalArgumentException("Author not found with id " + book.getAuthorID());
            }
            bookPersistent.setAuthor(authorPersistent);
        }

        var updated = libraryService.updateBook(request.getBookId(), bookPersistent);
        if (updated == null) {
            throw new IllegalArgumentException("Book not found with id " + request.getBookId());
        }

        var response = new UpdateBookResponse();
        response.setBook(mapToJaxbBook(updated));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteBookRequest")
    @ResponsePayload
    public DeleteBookResponse deleteBook(@RequestPayload DeleteBookRequest request) {
        libraryService.deleteBook(request.getBookId());

        var response = new DeleteBookResponse();
        response.setMessage("Book deleted");

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAuthorRequest")
    @ResponsePayload
    public GetAuthorResponse getAuthor(@RequestPayload GetAuthorRequest request) {
        var authorPersistent = libraryService.getAuthor(request.getAuthorId());

        if (authorPersistent == null) {
            throw new IllegalArgumentException("Author not found with id " + request.getAuthorId());
        }

        var response = new GetAuthorResponse();
        response.setAuthor(mapToJaxbAuthor(authorPersistent));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createAuthorRequest")
    @ResponsePayload
    public CreateAuthorResponse createAuthor(@RequestPayload CreateAuthorRequest request) {
        var author = request.getAuthor();

        if (author == null) {
            throw new IllegalArgumentException("Author must not be null");
        }

        validateNonEmpty(author.getName(), "name");
        validateSingleWord(author.getName(), "name");
        validateNonEmpty(author.getSurname(), "surname");
        validateSingleWord(author.getSurname(), "surname");

        var authorPersistent = new AuthorPersistent();
        authorPersistent.setName(author.getName());
        authorPersistent.setSurname(author.getSurname());

        var saved = libraryService.createAuthor(authorPersistent);

        var response = new CreateAuthorResponse();
        response.setAuthor(mapToJaxbAuthor(saved));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAuthorRequest")
    @ResponsePayload
    public DeleteAuthorResponse deleteAuthor(@RequestPayload DeleteAuthorRequest request) {
        libraryService.deleteAuthor(request.getAuthorId());

        var response = new DeleteAuthorResponse();
        response.setMessage("Author deleted");

        return response;
    }

    private Book mapToJaxbBook(BookPersistent persistent) {
        var book = new Book();
        book.setId(persistent.getId());
        book.setTitle(persistent.getTitle());
        if (persistent.getAuthor() != null) {
            book.setAuthorID(persistent.getAuthor().getId());
        }
        return book;
    }

    private Author mapToJaxbAuthor(AuthorPersistent persistent) {
        var author = new Author();
        author.setId(persistent.getId());
        author.setName(persistent.getName());
        author.setSurname(persistent.getSurname());
        return author;
    }

    private void validateNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
    }

    private void validateSingleWord(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
        var parts = value.trim().split("\\s+");
        if (parts.length != 1) {
            throw new IllegalArgumentException(fieldName + " must be a single word");
        }
    }
}