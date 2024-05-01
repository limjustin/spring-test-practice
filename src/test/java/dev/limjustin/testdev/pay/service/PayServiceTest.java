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

    private User user;

    @BeforeEach
    void setUp() {
        // user 생성 코드가 중복되므로, 인스턴스 변수로 선언하여 정의
        // @BeforeEach : 각 테스트 메서드가 실행되기 전 무조건 1번 실행되는 메서드 (영어 그대로 해석해도 좋아)
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
        // given (반환용 List<Pay> 생성 후, 서비스 코드 메서드 내에 필요한 stub 정의)
        List<Pay> pays = new ArrayList<>();
        pays.add(createPay(user, "My Pay 1"));
        pays.add(createPay(user, "My Pay 2"));
        pays.add(createPay(user, "My Pay 3"));

        Long userId = 1L;
        Mockito.when(payRepository.findByUser_Id(1L)).thenReturn(pays);

        // when (서비스 코드 결과값 반환)
        List<Pay> results = payService.findAllByUserId(userId);

        // then (결과값으로 받은 List<Pay> 크기와 테스트 코드에서 정의한 List<Pay> 크기 비교)
        assertEquals(results.size(), pays.size());
    }

    @Test
    @DisplayName("페이 등록 : 정상")
    void givenUser_whenAddPay_thenPayLengthIncrease() {
        // given (서비스 코드 메서드 내에 필요한 stub 정의)
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));  // 인스턴스 변수 user 사용

        // when (새로운 페이 등록하고, 서비스 코드 결과값 반환)
        String payAlias = "My_Pay_1";
        Pay myPay1 = payService.createPay(1L, payAlias);

        // then (결과값으로 받은 Pay 객체의 정보가 테스트 코드에서 정의한 정보와 같은지 비교)
        assertAll(
                () -> assertEquals(myPay1.getUser(), user),
                () -> assertEquals(myPay1.getAlias(), payAlias)
        );
    }

    @Test
    @DisplayName("페이 등록 : 예외 - 최대 보유 개수 초과")
    void givenUserAddPayThreeTimes_whenAddPay_thenThrowException() {
        // given (서비스 코드 메서드 내에 필요한 stub 정의 후, 3개의 페이 등록)
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String payAlias1 = "My_Pay_1";
        payService.createPay(1L, payAlias1);
        String payAlias2 = "My_Pay_2";
        payService.createPay(1L, payAlias2);
        String payAlias3 = "My_Pay_3";
        payService.createPay(1L, payAlias3);

        // when (4번째 페이 등록을 위한 페이 닉네임 -> 사실 여기서는 when 절이 애매하다)
        String payAlias4 = "My_Pay_4";

        // then (페이 등록 최대 개수를 넘겼으므로 예외 발생)
        assertThrows(RuntimeException.class, () -> payService.createPay(1L, payAlias4));
    }

    @Test
    @DisplayName("페이 제거 : 정상")
    void givenUserAddPay_whenRemovePay_thenUserPayLengthDecrease() {
        // given (서비스 코드 메서드 내에 필요한 stub 정의 후, 1개의 새로운 페이 등록, 등록한 페이도 stub 정의)
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        String payAlias = "My_Pay_1";
        Pay myPay1 = payService.createPay(1L, payAlias);

        Mockito.when(payRepository.findById(10L)).thenReturn(Optional.of(myPay1));  // 10L : 임의의 아이디 값임!

        // when (페이 삭제를 요청)
        payService.removePay(1L, 10L);

        // then (등록된 페이가 1개였는데 없어졌으니 0개인지 확인)
        assertEquals(user.getPayLength(), 0);
    }

    @Test
    @DisplayName("페이 제거 : 예외 - 페이가 0개일 때는 제거 불가")
    void givenUserNoPay_whenRemovePay_thenThrowException() {
        // given (서비스 코드 메서드 내에 필요한 stub 정의 후)
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // then (등록된 페이가 현재 없으므로 예외 발생)
        assertThrows(RuntimeException.class, () -> payService.removePay(1L, 10L));
    }

    @Test
    @DisplayName("페이 충전 : 정상")
    void givenUserAddPay_whenChargePay_thenReturnPayAndBalanceIncrease() {
        // given (서비스 코드 메서드 내에 필요한 stub 정의 후, 1개의 새로운 페이 등록, 등록한 페이도 stub 정의)
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        String payAlias = "My_Pay_1";
        Pay myPay1 = payService.createPay(1L, payAlias);
        int currentBalance = myPay1.getBalance();

        Mockito.when(payRepository.findById(10L)).thenReturn(Optional.of(myPay1));

        // when (충전 금액을 설정하고, 충전 메서드 호출하고 Pay 객체 반환)
        int chargePrice = 10000;
        Pay pay = payService.chargePay(10L, chargePrice);

        // then (반환 받은 Pay 객체의 잔고와 테스트 코드에서 예상하는 잔고가 똑같은지 비교)
        assertEquals(pay.getBalance(), currentBalance + chargePrice);
    }

    @Test
    @DisplayName("페이 충전 : 예외 - 충전 금액은 무조건 양수")
    void given_whenNegativeChargePrice_thenThrowException() {
        // when (충전 금액을 음수로 설정)
        int chargePrice = -10000;

        // then (음수를 충전 금액으로 하였으니, 바로 예외 처리 잡혀버림. 그래서 stub 정의 필요 없는 것이다!)
        assertThrows(RuntimeException.class, () -> payService.chargePay(10L, chargePrice));
    }

    private User createUser(String name, String nickname) {
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