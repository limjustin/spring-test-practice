package dev.limjustin.testdev.user.domain;

import dev.limjustin.testdev.pay.domain.Pay;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@Getter
@Entity
public class User {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String nickname;  // 요구사항 : 닉네임 최대 길이 15자까지

    @OneToMany(mappedBy = "user")  // 요구사항 : 사용자의 최대 페이 보유 개수는 3개
    private List<Pay> pays;

    @Builder
    public User(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
        this.pays = new ArrayList<>();
    }

    public void addPay(Pay pay) {
        this.pays.add(pay);
    }

    public void removePay(Pay pay) {
        if (pays.isEmpty())
            throw new RuntimeException();
        this.pays.remove(pay);
    }

    public int getPayLength() {
        return pays.size();
    }
}
