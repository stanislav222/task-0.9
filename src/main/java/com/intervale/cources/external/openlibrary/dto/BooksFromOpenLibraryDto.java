package com.intervale.cources.external.openlibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BooksFromOpenLibraryDto {
    private List<BookFromOpenLibraryDto> entries;
}
