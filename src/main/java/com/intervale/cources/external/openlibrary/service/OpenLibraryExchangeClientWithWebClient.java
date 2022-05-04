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
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenLibraryExchangeClientWithWebClient {

    @Qualifier("webClientOpenLib")
    private final WebClient webClient;

    private final OpenLibraryConfigProperties libraryConfig;

    private static final String OL_ROUT = "/search/authors.json?q=";
    private static final String PART_OF_THE_REQUEST = "/authors/";
    private static final String LIMIT = "/works.json?limit=";

    public List<BookFromOpenLibraryDto> getBookFromOpenLibraryByAuthor(String authorName) {
        AuthorFromOpenLibDto authorDto = getAuthorKeyFromOpenLibrary(authorName);
        String url = PART_OF_THE_REQUEST+authorDto.getKey()+LIMIT+libraryConfig.getLimitRecord();
        BooksFromOpenLibraryDto books = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(BooksFromOpenLibraryDto.class)
                .blockOptional()
                .orElseThrow(() ->
                        new OpenLibException("The answer received " +
                                "from OpenLib is not correct: author"));
        books.getEntries().forEach(bookFromOpenLibraryDto -> bookFromOpenLibraryDto.setDocs(authorDto));
        return books.getEntries();
    }

    public AuthorFromOpenLibDto getAuthorKeyFromOpenLibrary(String authorName){
        String url = OL_ROUT + authorName;
        String result = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .blockOptional()
                .orElseThrow(() -> new OpenLibException("The answer received " +
                        "from OpenLib is not correct: key"));
        JSONArray jsonArray = new JSONObject(result)
                .getJSONArray("docs");
        return new AuthorFromOpenLibDto(jsonArray.getJSONObject(0).getString("key"),
                jsonArray.getJSONObject(0).getString("name"));
    }
}
