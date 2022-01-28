package com.example.demo.sevice;

import com.example.demo.external.OpenLibraryExchangeClient;
import com.example.demo.external.dto.AuthorFromOpenLibDto;
import com.example.demo.external.dto.BookFromOpenLibraryDto;
import com.example.demo.external.dto.BooksFromOpenLibraryDto;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenLibraryExchangeClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenLibraryExchangeClient openLibraryExchangeClient;

    private final String authorJson = "{\n" +
            "\"docs\": [\n" +
            "{\n" +
            "\"key\": \"OL23919A\",\n" +
            "\"type\": \"author\",\n" +
            "\"name\": \"J. K. Rowling\",\n" +
            "\"alternate_names\": [\n" +
            "]\n" + "}\n" + "]\n" + "}" ;

    //можно любой все равно мокаю
    private final String urlGetBookByAuthor = "https://openlibrary.org/authors/" + "OL23919A" + "/works.json?limit=10";
    private final String urlGetKeyAuthor = "https://openlibrary.org/search/authors.json?q=rowling";

    @Test
    @SneakyThrows
    void getBookFromOpenLibraryByAuthor() {
        BookFromOpenLibraryDto libraryDtoForTest = new BookFromOpenLibraryDto(
                new AuthorFromOpenLibDto("key", "name"),
                "title",
                "key");
        BooksFromOpenLibraryDto booksFromOpenLibraryDto = new BooksFromOpenLibraryDto();
        booksFromOpenLibraryDto.setEntries(List.of(libraryDtoForTest));
        when(restTemplate.getForObject(new URI(urlGetBookByAuthor), BooksFromOpenLibraryDto.class))
                .thenReturn(booksFromOpenLibraryDto);
        when(restTemplate.getForEntity(new URI(urlGetKeyAuthor), String.class))
                .thenReturn(ResponseEntity.ok().body(authorJson));

        List<BookFromOpenLibraryDto> fromOpenLibraryByAuthor = openLibraryExchangeClient.getBookFromOpenLibraryByAuthor("rowling");
        fromOpenLibraryByAuthor.forEach(bookFromOpenLibraryDto -> {
            bookFromOpenLibraryDto.setDocs(new AuthorFromOpenLibDto("key", "rowling"));
            bookFromOpenLibraryDto.setKey("key");
            bookFromOpenLibraryDto.setTitle("title");
        });
        List<BookFromOpenLibraryDto> listForComparison = List.of(libraryDtoForTest);
        assertEquals(listForComparison, fromOpenLibraryByAuthor);
    }

    @Test
    @SneakyThrows
    void getAuthorKeyFromOpenLibrary() {
        AuthorFromOpenLibDto authorFromOpenLibDto = new AuthorFromOpenLibDto("OL23919A", "J. K. Rowling");
        JSONArray jsonArray = new JSONObject(authorJson).getJSONArray("docs");
        when(restTemplate.getForEntity(new URI(urlGetKeyAuthor), String.class))
                .thenReturn(ResponseEntity.ok().body(authorJson));
        AuthorFromOpenLibDto author = openLibraryExchangeClient.getAuthorKeyFromOpenLibrary("rowling");
        author.setKey(jsonArray.getJSONObject(0).getString("key"));
        author.setName(jsonArray.getJSONObject(0).getString("name"));
        assertEquals(authorFromOpenLibDto, author);
    }
}