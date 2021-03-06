package com.intervale.cources.model.dto;

import com.intervale.cources.util.BookJsonDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = BookJsonDeserializer.class)
@Schema(name = "BookResponse", description = "Выходная модель, содержащая основные поля книги")
public class BookDto {
    @JsonIgnore
    private Integer id;
    @NotNull
    @NotBlank(message = "isbn is mandatory")
    private String isbn;
    @NotNull
    @NotEmpty(message = "title is mandatory")
    private String title;
    @NotNull
    @NotBlank(message = "author is mandatory")
    private String author;
    @NotNull
    @NotBlank(message = "sheets is mandatory")
    private String sheets;
    @NotNull
    @NotBlank(message = "weight is mandatory")
    private String weight;
    @NotNull
    @Positive
    private BigDecimal cost;

    public BookDto(@NotBlank(message = "isbn is mandatory") String isbn,
                   @NotEmpty(message = "title is mandatory") String title, @NotBlank(message = "author is mandatory") String author, @NotBlank(message = "sheets is mandatory") String sheets, @NotBlank(message = "weight is mandatory") String weight, BigDecimal cost) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.sheets = sheets;
        this.weight = weight;
        this.cost = cost;
    }
}
