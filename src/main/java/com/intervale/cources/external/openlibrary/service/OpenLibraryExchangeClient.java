package com.intervale.cources.external.openlibrary.service;
import com.intervale.cources.exception.OpenLibException;
import com.intervale.cources.external.openlibrary.config.OpenLibraryConfigProperties;
import com.intervale.cources.external.openlibrary.dto.AuthorFromOpenLibDto;
import com.intervale.cources.external.openlibrary.dto.BookFromOpenLibraryDto;
import com.intervale.cources.external.openlibrary.dto.BooksFromOpenLibraryDto;
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

    private static final String OL_ROUT = "/search/authors.json?q=";
    private static final String PART_OF_THE_REQUEST = "/authors/";
    private static final String LIMIT = "/works.json?limit=";


    public List<BookFromOpenLibraryDto> getBookFromOpenLibraryByAuthor(String authorName) {
        AuthorFromOpenLibDto authorDto = getAuthorKeyFromOpenLibrary(authorName);
        String url = PART_OF_THE_REQUEST +authorDto.getKey() + LIMIT +libraryConfig.getLimitRecord();
        BooksFromOpenLibraryDto response = restTemplate.getForObject(url, BooksFromOpenLibraryDto.class);
        if(response == null){
            throw new OpenLibException("The answer received from OpenLib is not correct");
        }
        response.getEntries().forEach(bookFromOpenLibraryDto -> bookFromOpenLibraryDto.setDocs(authorDto));
        return response.getEntries();
    }

    public AuthorFromOpenLibDto getAuthorKeyFromOpenLibrary(String authorName){
        String url =  OL_ROUT + authorName;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONArray jsonArray = new JSONObject(response.getBody())
                    .getJSONArray("docs");
        return new AuthorFromOpenLibDto(jsonArray.getJSONObject(0).getString("key"),
                    jsonArray.getJSONObject(0).getString("name"));
    }
}
