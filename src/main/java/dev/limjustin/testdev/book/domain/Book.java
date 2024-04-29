package dev.limjustin.testdev.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@Getter
@Entity
public class Book {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title;
    private int price;
    private int quantity;

    @Builder
    public Book(String title, int price, int quantity) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public void minusQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public void plusQuantity(int quantity) {
        this.quantity += quantity;
    }
}
