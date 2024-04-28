package dev.limjustin.testdev.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@Entity
public class Book {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title;
    private int price;

    public Book(String title, int price) {
        this.title = title;
        this.price = price;
    }
}
