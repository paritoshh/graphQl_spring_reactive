package com.prac.graphql.model;


import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("books")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Book {

    @Id
    private UUID id;
    private int pages;
    private String name;
    private Category category;

}
