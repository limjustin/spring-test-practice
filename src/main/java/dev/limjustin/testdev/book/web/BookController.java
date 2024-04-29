package dev.limjustin.testdev.book.web;

import dev.limjustin.testdev.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;
}
