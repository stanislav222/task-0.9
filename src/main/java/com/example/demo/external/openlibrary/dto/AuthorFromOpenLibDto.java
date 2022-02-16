package com.example.demo.external.openlibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorFromOpenLibDto {
    private String key;
    private String name;
}
