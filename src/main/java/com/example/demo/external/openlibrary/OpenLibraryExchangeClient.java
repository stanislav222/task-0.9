package com.example.demo.external.openlibrary;
import com.example.demo.external.openlibrary.config.OpenLibraryConfigProperties;
import com.example.demo.external.openlibrary.dto.AuthorFromOpenLibDto;
import com.example.demo.external.openlibrary.dto.BookFromOpenLibraryDto;
import com.example.demo.external.openlibrary.dto.BooksFromOpenLibraryDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenLibraryExchangeClient {

    private final OpenLibraryConfigProperties libraryConfig;

    @Qualifier("openLibRestTemplate")
    private final RestTemplate restTemplate;

    public List<BookFromOpenLibraryDto> getBookFromOpenLibraryByAuthor(String authorName) {
        AuthorFromOpenLibDto authorDto = getAuthorKeyFromOpenLibrary(authorName);
        String url = "/authors/"+authorDto.getKey()+"/works.json?limit="+libraryConfig.getLimitRecord();
        BooksFromOpenLibraryDto response = restTemplate.getForObject(url, BooksFromOpenLibraryDto.class);
        assert response != null;
        response.getEntries().forEach(bookFromOpenLibraryDto -> bookFromOpenLibraryDto.setDocs(authorDto));
        return response.getEntries();
    }

    public AuthorFromOpenLibDto getAuthorKeyFromOpenLibrary(String authorName){
        String url = "/search/authors.json?q="+authorName;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONArray jsonArray = new JSONObject(response.getBody())
                    .getJSONArray("docs");
        return new AuthorFromOpenLibDto(jsonArray.getJSONObject(0).getString("key"),
                    jsonArray.getJSONObject(0).getString("name"));
    }
}
