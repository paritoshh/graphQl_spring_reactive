package com.prac.graphql;

import com.prac.graphql.service.AuthorService;
import com.prac.graphql.service.BookService;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import io.r2dbc.spi.ConnectionFactory;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;

@SpringBootApplication
public class GraphqlApplication {

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;

    public static void main(String[] args) {
        SpringApplication.run(GraphqlApplication.class, args);
    }

    @Bean
    public ConnectionFactoryInitializer connectionFactoryInitializer(
        ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer connectionFactoryInitializer = new ConnectionFactoryInitializer();
        connectionFactoryInitializer.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(
            new ClassPathResource("schema.sql")
          //  ,new ClassPathResource("data.sql")

            /**
             * data.sql
             *
             * --INSERT INTO books(id, name, pages) VALUES ('e52232e1-0ded-4587-999f-4dd135a4a94f','My First Book', 100);
             * --INSERT INTO books(name, pages) VALUES ('My Second Book', 200);
             * --INSERT INTO books(name, pages) VALUES ('My Third Book', 300);
             * --INSERT INTO books(name, pages) VALUES ('My Forth Book', 400);
             * --INSERT INTO books(name, pages) VALUES ('My Fifth Book', 500);
             */
        );
        connectionFactoryInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return connectionFactoryInitializer;
    }

    @Bean
    public GraphQL graphQL() throws IOException {

        //Schema Parser
        SchemaParser schemaParser = new SchemaParser();
        ClassPathResource schema = new ClassPathResource("schema.graphql");
        //TypeDefinition Registry
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema.getInputStream());

        //Type Wiring & Runtime wiring
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("getBook", bookService.getBook()))
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("getBooks", bookService.getBooks()))
            .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                .dataFetcher("createBook", bookService.createBook()))
            .type(TypeRuntimeWiring.newTypeWiring("Book")
                .dataFetcher("author", authorService.authorDataFetcher()))
            .build();

        //Schema Generator
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator
            .makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        //Grapgh QL
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

}
