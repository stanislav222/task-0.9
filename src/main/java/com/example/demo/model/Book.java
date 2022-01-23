package com.example.demo.model;

import com.example.demo.util.BookJsonDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = BookJsonDeserializer.class)
public class Book {
    @JsonIgnore
    private Integer id;
    @NotBlank(message = "isbn is mandatory")
    private String isbn;
    @NotEmpty(message = "title is mandatory")
    private String title;
    @NotBlank(message = "author is mandatory")
    private String author;
    @NotBlank(message = "sheets is mandatory")
    private String sheets;
    @NotBlank(message = "weight is mandatory")
    private String weight;
    @Positive
    private BigDecimal cost;

    public Book(@NotBlank(message = "isbn is mandatory") String isbn, @NotEmpty(message = "title is mandatory") String title, @NotBlank(message = "author is mandatory") String author, @NotBlank(message = "sheets is mandatory") String sheets, @NotBlank(message = "weight is mandatory") String weight, BigDecimal cost) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.sheets = sheets;
        this.weight = weight;
        this.cost = cost;
    }
}
