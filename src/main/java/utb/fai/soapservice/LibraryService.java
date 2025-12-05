package utb.fai.soapservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utb.fai.soapservice.Model.AuthorPersistent;
import utb.fai.soapservice.Model.BookPersistent;
import utb.fai.soapservice.Repository.AuthorRepository;
import utb.fai.soapservice.Repository.BookRepository;

/**
 * Servis obsahujici aplikacni logiky pro praci s daty (knihy a autori)
 */
@Service
public class LibraryService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public AuthorPersistent getAuthor(long authorId) {
        return authorRepository.findById(authorId).orElse(null);
    }

    public BookPersistent getBook(long bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }

    public BookPersistent createBook(BookPersistent book) {
        return bookRepository.save(book);
    }

    public void deleteBook(long bookId) {
        bookRepository.deleteById(bookId);
    }

    public AuthorPersistent createAuthor(AuthorPersistent author) {
        return authorRepository.save(author);
    }

    public void deleteAuthor(long authorId) {
        authorRepository.deleteById(authorId);
    }

    public BookPersistent updateBook(long bookId, BookPersistent newBookData) {
        BookPersistent existing = bookRepository.findById(bookId).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setTitle(newBookData.getTitle());
        existing.setAuthor(newBookData.getAuthor());
        return bookRepository.save(existing);
    }
}