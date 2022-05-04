package com.intervale.cources.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("api/test")
@Tag(name = "Controller for the second task", description = "Task 2")
public class TestController {

    @GetMapping("/hello")
    ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello!", HttpStatus.OK);
    }

    @GetMapping("/withParams")
    public ResponseEntity<String> withParams(@RequestParam Optional<String> singleParamName){
        return new ResponseEntity<>("ParamName: " + singleParamName
                .orElseGet(() -> "Enter the parameter"), HttpStatus.ACCEPTED);
    }

    @GetMapping("/withPathVariable/{id}")
    ResponseEntity<String> withPathVariable(@PathVariable("id") int id) {
        return new ResponseEntity<>(
                "201 for" + id,
                HttpStatus.CREATED);
    }

    @PostMapping(path = "/echo",
            produces={"application/json","application/xml"}, consumes="text/html")
        public String create(@RequestHeader(value = "Content-Type")
                              String header) {
        return "200 " + (header.equals("application/json") ? "json" : "xml");
    }

    @PutMapping("/put")
    ResponseEntity<String> myPut() {
        return ResponseEntity.ok("200 ok");
    }

    @Operation(summary = "Set and update time with cookie")
    @GetMapping("/cookie")
    public ResponseEntity setAndUpdateCookie(@CookieValue(value = "time",
            defaultValue = "Cookies is created") String time) {
        String timeStamp = new SimpleDateFormat("HH_mm_ss").format(Calendar.getInstance().getTime());
        var cookie = ResponseCookie.from("time", timeStamp).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(time);
    }

}

