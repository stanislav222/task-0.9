package com.example.demo.util;

import com.example.demo.model.dto.BookDto;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class BookJsonDeserializer extends JsonDeserializer<BookDto> {

    // name + surname = author into model Book
    //json for test
    // {
    //    "isbn": "3",
    //        "title": "2",
    //        "name": "Ivan",
    //        "surname": "Ivanov",
    //        "sheets": "200",
    //        "weight": "5555",
    //        "cost": 32.222
    // }

    @Override
    public BookDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);

        TextNode isbn =  (TextNode) treeNode.get("isbn");
        TextNode title =  (TextNode) treeNode.get("title");
        TextNode name =  (TextNode) treeNode.get("name");
        TextNode surname =  (TextNode) treeNode.get("surname");
        TextNode sheets =  (TextNode) treeNode.get("sheets");
        TextNode weight =  (TextNode) treeNode.get("weight");
        DoubleNode cost =  (DoubleNode) treeNode.get("cost");

        return new BookDto(isbn.asText(),
                title.asText(),
                name.asText() + " " + surname.asText(),
                sheets.asText(),
                weight.asText(),
                cost.decimalValue());
    }
}
