package com.prac.graphql.service;

import com.prac.graphql.model.Author;
import com.prac.graphql.model.Book;
import com.prac.graphql.repository.AuthorRepository;
import graphql.schema.DataFetcher;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    public Mono<String> createAuthor(String authorName, int age, UUID bookId){
        Author author = Author.builder()
            .age(age)
            .name(authorName)
            .bookId(bookId)
            .build();

        return authorRepository.createAuthor(author).map(Object::toString);

    }

    public DataFetcher<CompletableFuture<Author>> authorDataFetcher(){
        return env->{
           Book book = env.getSource();
           return authorRepository.fetchAuthor(book.getId()).toFuture();
        };
    }

}
