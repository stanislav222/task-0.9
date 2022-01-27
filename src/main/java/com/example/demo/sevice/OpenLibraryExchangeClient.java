package com.example.demo.sevice;
import com.example.demo.model.dto.AuthorFromOpenLibDto;
import com.example.demo.model.dto.BookFromOpenLibraryDto;
import com.example.demo.model.dto.BooksFromOpenLibraryDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
@Slf4j
public class OpenLibraryExchangeClient {

    private static final Logger logger = LoggerFactory.getLogger(OpenLibraryExchangeClient.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public List<BookFromOpenLibraryDto> getBookFromOpenLibraryByAuthor(String authorName) {
        AuthorFromOpenLibDto authorDto = getAuthorKeyFromOpenLibrary(authorName);
        String url = "https://openlibrary.org/authors/"+authorDto.getKey()+"/works.json?limit=10";
        BooksFromOpenLibraryDto response = null;
        try {
            response = restTemplate.getForObject(new URI(url), BooksFromOpenLibraryDto.class);
            val entries = response.getEntries();
            entries.forEach(bookFromOpenLibraryDto -> bookFromOpenLibraryDto.setDocs(authorDto));
        } catch (URISyntaxException e) {
            logger.error("Some link is bad " + e);
        }
        return response.getEntries();
    }

    public AuthorFromOpenLibDto getAuthorKeyFromOpenLibrary(String authorName){
        String url = "https://openlibrary.org/search/authors.json?q="+authorName;
        ResponseEntity<String> response = null;
        JSONArray jsonArray = new JSONArray();
        try {
            response = restTemplate.getForEntity(new URI(url), String.class);
            jsonArray = new JSONObject(response.getBody())
                    .getJSONArray("docs");

        } catch (URISyntaxException e) {
            logger.error("Some link is bad " + e);
        }
        return new AuthorFromOpenLibDto(jsonArray.getJSONObject(0).getString("key"),
                    jsonArray.getJSONObject(0).getString("name"));
    }


}
