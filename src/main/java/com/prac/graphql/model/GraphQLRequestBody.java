package com.prac.graphql.model;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class GraphQLRequestBody {

    private String query;
    private String operationName;
    private Map<String, Object> variables;

}
