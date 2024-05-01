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

    /**
     * userId 사용하여 사용자가 가지고 있는 모든 페이를 조회하는 메서드
     * @param userId
     * @return
     */
    public List<Pay> findAllByUserId(Long userId) {
        return payRepository.findByUser_Id(userId);
    }

    /**
     * userId 사용하여 사용자를 조회하고, 새로운 Pay 객체를 생성하고 사용자에게 등록하는 메서드
     * @param userId
     * @param alias
     * @return
     * @throws RuntimeException (단, 사용자가 가질 수 있는 페이의 개수(3개)를 넘으면 예외 발생)
     */
    public Pay createPay(Long userId, String alias) throws RuntimeException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        if (user.getPayLength() >= 3)
            throw new RuntimeException("Pay create limit is 3");

        Pay pay = createPayEntity(user, alias);
        user.addPay(pay);
        payRepository.save(pay);
        return pay;
    }

    /**
     * userId 사용하여 사용자를 조회하고, payId 사용하여 사용자에게 등록된 페이를 조회하고, 사용자가 가지고 있는 페이를 삭제하는 메서드
     * @param userId
     * @param payId
     * @throws RuntimeException (단, 사용자에게 등록된 페이가 없을 경우 예외 발생)
     */
    public void removePay(Long userId, Long payId) throws RuntimeException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        if (user.getPayLength() == 0)
            throw new RuntimeException("No Pay to remove");

        Pay pay = payRepository.findById(payId)
                .orElseThrow(() -> new RuntimeException("Pay with id " + payId + " not found"));

        user.removePay(pay);
    }

    /**
     * payId 사용하여 페이를 조회하고, 입력한 금액만큼 돈을 충전하는 메서드
     * @param payId
     * @param price
     * @return
     * @throws RuntimeException (단, 입력 금액이 음수일 경우 예외 발생)
     */
    public Pay chargePay(Long payId, int price) throws RuntimeException {
        if (price < 0)
            throw new RuntimeException("Price cannot be negative");

        Pay pay = payRepository.findById(payId)
                .orElseThrow(() -> new RuntimeException("Pay with id " + payId + " not found"));

        pay.charge(price);
        return pay;
    }

    /**
     * pay 객체 생성하는 private 메서드
     * @param user
     * @param alias
     * @return
     */
    private Pay createPayEntity(User user, String alias) {
        return Pay.builder()
                .user(user)
                .alias(alias)
                .build();
    }
}
