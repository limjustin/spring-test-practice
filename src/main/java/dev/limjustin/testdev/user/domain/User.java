package dev.limjustin.testdev.user.domain;

import dev.limjustin.testdev.pay.domain.Pay;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@Entity
public class User {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String nickname;

    @OneToMany(mappedBy = "user")
    private List<Pay> pays = new ArrayList<>();

    @Builder
    public User(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }

    public void addPay(Pay pay) {
        this.pays.add(pay);
    }

    public void removePay(Pay pay) {
        this.pays.remove(pay);
    }

    public int getPayLength() {
        return pays.size();
    }
}
