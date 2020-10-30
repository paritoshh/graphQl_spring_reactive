package com.prac.graphql.repository;


import com.prac.graphql.model.Author;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class AuthorRepository {

    @Autowired
    private DatabaseClient databaseClient;

    public Mono<UUID> createAuthor(Author author){
        UUID authorId = UUID.randomUUID();
        author.setId(authorId);

        return databaseClient.insert()
            .into(Author.class)
            .using(author)
            .then()
            .thenReturn(authorId);
    }

    public Mono<Author> fetchAuthor(UUID bookId){

        return databaseClient.select()
            .from(Author.class)
            .matching(Criteria.where("book_id").is(bookId))
            .fetch()
            .one();
    }

}
