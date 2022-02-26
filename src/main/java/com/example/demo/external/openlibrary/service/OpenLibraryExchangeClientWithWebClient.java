package com.example.demo.external.openlibrary.service;

import com.example.demo.external.openlibrary.config.OpenLibraryConfigProperties;
import com.example.demo.external.openlibrary.dto.AuthorFromOpenLibDto;
import com.example.demo.external.openlibrary.dto.BookFromOpenLibraryDto;
import com.example.demo.external.openlibrary.dto.BooksFromOpenLibraryDto;
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

    public List<BookFromOpenLibraryDto> getBookFromOpenLibraryByAuthor(String authorName) {
        AuthorFromOpenLibDto authorDto = getAuthorKeyFromOpenLibrary(authorName);
        String url = "/authors/"+authorDto.getKey()+"/works.json?limit="+libraryConfig.getLimitRecord();
        BooksFromOpenLibraryDto books = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(BooksFromOpenLibraryDto.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Something went wrong"));
        books.getEntries().forEach(bookFromOpenLibraryDto -> bookFromOpenLibraryDto.setDocs(authorDto));
        return books.getEntries();
    }

    public AuthorFromOpenLibDto getAuthorKeyFromOpenLibrary(String authorName){
        String url = "/search/authors.json?q="+authorName;
        String result = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Something went wrong"));
        JSONArray jsonArray = new JSONObject(result)
                .getJSONArray("docs");
        return new AuthorFromOpenLibDto(jsonArray.getJSONObject(0).getString("key"),
                jsonArray.getJSONObject(0).getString("name"));
    }
}
