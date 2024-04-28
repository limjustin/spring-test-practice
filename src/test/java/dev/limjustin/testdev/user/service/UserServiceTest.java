package dev.limjustin.testdev.user.service;

import dev.limjustin.testdev.pay.domain.Pay;
import dev.limjustin.testdev.user.domain.User;
import dev.limjustin.testdev.user.domain.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("사용자 등록 : 정상")
    void givenUser_whenJoinUser_thenReturnUser() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        // when
        User joinedUser = userService.join(user);

        // then
        assertEquals(joinedUser.getName(), userName);
    }

    @Test
    @DisplayName("사용자 등록 : 예외 - 닉네임 길이 제한 초과")
    void givenUserWithLongNickname_whenJoinUser_thenThrowException() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "JayceJayceJayceJayceJayceJayceJayce";
        User user = createUser(userName, userNickname);

        // then
        assertThrows(IllegalArgumentException.class, () -> userService.join(user));
    }

    @Test
    @DisplayName("페이 등록 : 정상")
    void givenUser_whenAddPay_thenPayLengthIncrease() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);

        // when
        Pay myPay1 = createPay(user, "My_Pay_1");
        user.addPay(myPay1);

        // then
        assertEquals(user.getPayLength(), 1);
    }

    @Test
    @DisplayName("페이 등록 : 예외 - 최대 보유 개수 초과")
    void givenUserAddPayThreeTimes_whenAddPay_thenThrowException() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);

        Pay myPay1 = createPay(user, "My_Pay_1");
        Pay myPay2 = createPay(user, "My_Pay_2");
        Pay myPay3 = createPay(user, "My_Pay_3");
        user.addPay(myPay1);
        user.addPay(myPay2);
        user.addPay(myPay3);

        // when
        Pay myPay4 = createPay(user, "My_Pay_4");

        // then
        assertThrows(RuntimeException.class, () -> user.addPay(myPay4));
    }

    @Test
    @DisplayName("페이 제거 : 정상")
    void givenUserAddPay_whenRemovePay_thenPayLengthDecrease() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);

        Pay myPay1 = createPay(user, "My_Pay_1");
        user.addPay(myPay1);

        // when
        user.removePay(myPay1);

        // then
        assertEquals(user.getPayLength(), 0);
    }

    @Test
    @DisplayName("페이 제거 : 예외 - 페이 0개일 때는 제거 불가")
    void givenUser_whenRemovePay_thenThrowException() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);

        // then
        assertThrows(RuntimeException.class, () -> user.removePay(createPay(user, "My_Pay_1")));
    }

    @Test
    @DisplayName("페이 충전 : 정상")
    void givenUserAddPay_whenChargePay_thenBalanceIncrease() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);

        Pay myPay1 = createPay(user, "My_Pay_1");
        user.addPay(myPay1);

        // when
        int chargePrice = 10000;
        int currentBalance = myPay1.getBalance();
        myPay1.charge(chargePrice);

        // then
        int expectBalance = currentBalance + chargePrice;
        assertEquals(myPay1.getBalance(), expectBalance);
    }

    @Test
    @DisplayName("페이 충전 : 예외 - 충전 금액은 무조건 양수")
    void givenUserAddPay_whenChargePayNegative_thenThrowException() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);

        Pay myPay1 = createPay(user, "My_Pay_1");
        user.addPay(myPay1);

        // when
        int chargePrice = -10000;

        // then
        assertThrows(IllegalArgumentException.class, () -> myPay1.charge(chargePrice));
    }

    @Test
    @DisplayName("페이 결제 : 정상")
    void givenUserAddPayAndCharge_whenPay_thenBalanceDecrease() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);

        Pay myPay1 = createPay(user, "My_Pay_1");
        user.addPay(myPay1);

        int chargePrice = 10000;
        myPay1.charge(chargePrice);

        // when
        int currentBalance = myPay1.getBalance();
        int payPrice = 3000;
        myPay1.pay(payPrice);

        // then
        int expectBalance = currentBalance - payPrice;
        assertEquals(myPay1.getBalance(), expectBalance);
    }

    @Test
    @DisplayName("페이 결제 : 예외 - 충전 금액은 무조건 양수")
    void givenUserAddPayAndCharge_whenPayNegative_thenThrowException() {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);

        Pay myPay1 = createPay(user, "My_Pay_1");
        user.addPay(myPay1);

        int chargePrice = 10000;
        myPay1.charge(chargePrice);

        // when
        int payPrice = -3000;

        // then
        assertThrows(IllegalArgumentException.class, () -> myPay1.pay(payPrice));
    }

    @Test
    @DisplayName("페이 결제 : 예외 - 잔고 부족")
    void givenUserAddPayAndCharge_whenPayOverBalance_thenThrowException () {
        // given
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        User user = createUser(userName, userNickname);

        Pay myPay1 = createPay(user, "My_Pay_1");
        user.addPay(myPay1);

        int chargePrice = 10000;
        myPay1.charge(chargePrice);

        // when
        int payPrice = 13000;

        // then
        assertThrows(IllegalArgumentException.class, () -> myPay1.pay(payPrice));
    }

    public User createUser(String name, String nickname) {
        return User.builder()
                .name(name)
                .nickname(nickname)
                .build();
    }

    public Pay createPay(User user, String alias) {
        return Pay.builder()
                .user(user)
                .alias(alias)
                .build();
    }
}