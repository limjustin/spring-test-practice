package dev.limjustin.testdev.pay.service;

import dev.limjustin.testdev.pay.domain.Pay;
import dev.limjustin.testdev.pay.domain.PayRepository;
import dev.limjustin.testdev.user.domain.User;
import dev.limjustin.testdev.user.domain.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PayServiceTest {

    @InjectMocks
    private PayService payService;

    @Mock
    private PayRepository payRepository;

    @Mock
    private UserRepository userRepository;

    private User user;  // static 변수 아님

    @BeforeEach
    void setUp() {
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        user = createUser(userName, userNickname);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * 테스트 케이스 정리
     * [v] 페이 조회 : 사용자가 등록한 페이 불러오기
     * [v] 페이 등록 : 정상
     * [v] 페이 등록 : 예외 - 최대 보유 개수 초과
     * [v] 페이 제거 : 정상
     * [v] 페이 제거 : 예외 - 페이가 0개일 때는 제거 불가
     * [v] 페이 충전 : 정상
     * [v] 페이 충전 : 예외 - 충전 금액은 무조건 양수
     */

    @Test
    @DisplayName("페이 조회 : 정상")
    void givenUserId_whenFindPayByUserId_thenReturnPayList() {
        // given
        List<Pay> pays = new ArrayList<>();
        pays.add(createPay(user, "My Pay 1"));
        pays.add(createPay(user, "My Pay 2"));
        pays.add(createPay(user, "My Pay 3"));

        Long userId = 1L;
        Mockito.when(payRepository.findByUser_Id(userId)).thenReturn(pays);

        // when
        List<Pay> results = payService.findAllByUserId(userId);

        // then
        assertEquals(results.size(), pays.size());
    }

    @Test
    @DisplayName("페이 등록 : 정상")
    void givenUser_whenAddPay_thenPayLengthIncrease() {
        // given
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        String payAlias = "My_Pay_1";
        Pay myPay1 = payService.createPay(1L, payAlias);

        // then
        assertAll(
                () -> assertEquals(myPay1.getUser(), user),
                () -> assertEquals(myPay1.getAlias(), payAlias)
        );
    }

    @Test
    @DisplayName("페이 등록 : 예외 - 최대 보유 개수 초과")
    void givenUserAddPayThreeTimes_whenAddPay_thenThrowException() {
        // given
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        String payAlias1 = "My_Pay_1";
        payService.createPay(1L, payAlias1);
        String payAlias2 = "My_Pay_2";
        payService.createPay(1L, payAlias2);
        String payAlias3 = "My_Pay_3";
        payService.createPay(1L, payAlias3);

        // when
        String payAlias4 = "My_Pay_4";

        // then
        assertThrows(RuntimeException.class, () -> payService.createPay(1L, payAlias4));
    }

    @Test
    @DisplayName("페이 제거 : 정상")
    void givenUserAddPay_whenRemovePay_thenUserPayLengthDecrease() {
        // given
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        String payAlias = "My_Pay_1";
        Pay myPay1 = payService.createPay(1L, payAlias);

        Mockito.when(payRepository.findById(10L)).thenReturn(Optional.of(myPay1));

        // when
        payService.removePay(1L, 10L);

        // then
        assertEquals(user.getPayLength(), 0);
    }

    @Test
    @DisplayName("페이 제거 : 예외 - 페이가 0개일 때는 제거 불가")
    void givenUserNoPay_whenRemovePay_thenThrowException() {
        // given
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // then
        assertThrows(RuntimeException.class, () -> payService.removePay(1L, 10L));
    }

    @Test
    @DisplayName("페이 충전 : 정상")
    void givenUserAddPay_whenChargePay_thenReturnPayAndBalanceIncrease() {
        // given
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        String payAlias = "My_Pay_1";
        Pay myPay1 = payService.createPay(1L, payAlias);
        int currentBalance = myPay1.getBalance();

        Mockito.when(payRepository.findById(10L)).thenReturn(Optional.of(myPay1));

        // when
        int chargePrice = 10000;
        Pay pay = payService.chargePay(10L, chargePrice);

        // then
        assertEquals(pay.getBalance(), currentBalance + chargePrice);
    }

    @Test
    @DisplayName("페이 충전 : 예외 - 충전 금액은 무조건 양수")
    void given_whenNegativeChargePrice_thenThrowException() {
        // when
        int chargePrice = -10000;

        // then
        assertThrows(RuntimeException.class, () -> payService.chargePay(10L, chargePrice));
    }

    private static User createUser(String name, String nickname) {
        return User.builder()
                .name(name)
                .nickname(nickname)
                .build();
    }

    private Pay createPay(User user, String alias) {
        return Pay.builder()
                .user(user)
                .alias(alias)
                .build();
    }
}