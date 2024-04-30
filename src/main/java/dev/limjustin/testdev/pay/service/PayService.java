package dev.limjustin.testdev.pay.service;

import dev.limjustin.testdev.pay.domain.Pay;
import dev.limjustin.testdev.pay.domain.PayRepository;
import dev.limjustin.testdev.user.domain.User;
import dev.limjustin.testdev.user.domain.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PayService {

    private final PayRepository payRepository;
    private final UserRepository userRepository;

    public List<Pay> findAllByUserId(Long userId) {
        return payRepository.findByUser_Id(userId);
    }

    public Pay createPay(Long userId, String alias) throws RuntimeException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        if (user.getPayLength() >= 3)
            throw new RuntimeException("Pay create limit is 3");

        Pay pay = createPayEntity(user, alias);
        user.addPay(pay);
        return pay;
    }

    public void removePay(Long userId, Long payId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        if (user.getPayLength() == 0)
            throw new RuntimeException("No Pay to remove");

        Pay pay = payRepository.findById(payId)
                .orElseThrow(() -> new RuntimeException("Pay with id " + payId + " not found"));

        user.removePay(pay);
    }

    public Pay chargePay(Long payId, int price) {
        if (price < 0)
            throw new RuntimeException("Price cannot be negative");

        Pay pay = payRepository.findById(payId)
                .orElseThrow(() -> new RuntimeException("Pay with id " + payId + " not found"));

        pay.charge(price);
        return pay;
    }

    private Pay createPayEntity(User user, String alias) {
        return Pay.builder()
                .user(user)
                .alias(alias)
                .build();
    }
}
