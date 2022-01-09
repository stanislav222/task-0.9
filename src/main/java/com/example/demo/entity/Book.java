package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private Integer id;
    @NotBlank(message = "isbn is mandatory")
    private String isbn;
    @NotEmpty(message = "title enter name")
    private String title;
    @NotBlank(message = "author is mandatory")
    private String author;
    @NotBlank(message = "sheets is mandatory")
    private String sheets;
    @NotBlank(message = "weight is mandatory")
    private String weight;
    private BigDecimal cost;
}
