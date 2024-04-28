package dev.limjustin.testdev.pay.domain;

import dev.limjustin.testdev.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@Getter
@Entity
public class Pay {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "pay_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id")
    private User user;

    private int balance;
    private String alias;

    @Builder
    public Pay(User user, String alias) {
        this.user = user;
        this.alias = alias;
        this.balance = 0;
    }

    public void charge(int price) {
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative");

        this.balance += price;
    }

    public void pay(int price) {
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative");

        if (balance < price)
            throw new IllegalArgumentException("Balance cannot be less than price");

        this.balance -= price;
    }
}
