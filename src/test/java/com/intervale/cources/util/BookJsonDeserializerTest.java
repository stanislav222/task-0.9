package com.intervale.cources.util;

import com.intervale.cources.model.dto.BookDto;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class BookJsonDeserializerTest {

    String filledJson = "{\n" +
            "       \"isbn\": \"3-331-13-131\",\n" +
            "       \"title\": \"Hello world\",\n" +
            "       \"name\": \"Ivan\",\n" +
            "       \"surname\": \"Ivanov\",\n" +
            "       \"sheets\": \"200\",\n" +
            "       \"weight\": \"200\",\n" +
            "       \"cost\": 32.22\n" +
            "}";

    private final String emptyJson = "{\n" +
            "       \"title\": \"Hello world\",\n" +
            "       \"name\": \"Ivan\",\n" +
            "       \"surname\": \"Ivanov\",\n" +
            "       \"sheets\": \"200\",\n" +
            "       \"weight\": \"200\",\n" +
            "       \"cost\": 32.22\n" +
            "}";

    @Autowired
    JacksonTester<BookDto> jacksonTester;

    @SneakyThrows
    @Test
    void testDeserialize() {
        BookDto book = jacksonTester.parseObject(filledJson);
        Assertions.assertThat(book.getAuthor()).isEqualTo("Ivan Ivanov");
        Assertions.assertThat(book.getSheets()).isEqualTo("200 pages");
        Assertions.assertThat(book.getWeight()).isEqualTo("200 pounds");
    }

    @SneakyThrows
    @Test
    void testDeserializeWithException() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () ->{
            jacksonTester.parseObject(emptyJson).getIsbn();
        });
    }
}