package com.prac.graphql.service;

import com.prac.graphql.model.Author;
import com.prac.graphql.model.Book;
import com.prac.graphql.model.Category;
import com.prac.graphql.repository.BookRepository;
import graphql.schema.DataFetcher;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorService authorService;

    public DataFetcher<CompletableFuture<Book>> getBook() {
        return env -> {
            String bookId = env.getArgument("id");
            return bookRepository.getBook(bookId)
                .toFuture();
        };
    }


    public DataFetcher<CompletableFuture<List<Book>>> getBooks() {
        return env ->
            bookRepository.getBooks().collectList().toFuture();
    }

    public DataFetcher<CompletableFuture<String>> createBook() {
        return env -> {
            String bookName = env.getArgument("bookName");
            String authorName = env.getArgument("authorName");
            int bookPageNumber = env.getArgument("pages");
            int age = env.getArgument("age");
            Category category = Category.valueOf(env.getArgument("category"));

            Book book = Book.builder()
                .name(bookName)
                .pages(bookPageNumber)
                .category(category)
                .build();
            //.BUI(bookName, bookPageNumber, category);

            return bookRepository.createBook(book)
                .flatMap(bookId ->
                    authorService.createAuthor(authorName, age, bookId)
                        .map(authorId -> bookId.toString()))
                .toFuture();
                /*.map(Object::toString)
                .toFuture();*/
        };

    }
}
