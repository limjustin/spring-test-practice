package dev.limjustin.testdev.pay.domain;

import dev.limjustin.testdev.user.domain.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@Entity
public class Pay {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "pay_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id")
    private User user;

    private int balance;
    private String name;

    public void charge(Long price) {
        this.balance += price;
    }

    public void pay(Long price) {
        if (balance - price > 0)
            this.balance -= price;
    }
}
