package dev.limjustin.testdev.book.service;

import dev.limjustin.testdev.book.domain.Book;
import dev.limjustin.testdev.book.domain.BookRepository;
import dev.limjustin.testdev.pay.domain.Pay;
import dev.limjustin.testdev.pay.domain.PayRepository;
import dev.limjustin.testdev.user.domain.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PayRepository payRepository;

    private User user;

    private static final List<Book> testBooks = new ArrayList<>();

    @BeforeEach
    void setup() {
        String userName = "Jaeyoung";
        String userNickname = "Jayce";
        user = createUser(userName, userNickname);
    }

    @BeforeAll
    static void setUp() {
        String[] titles = new String[]{
                "Welcome to the Show", "Clean Code", "오브젝트", "Java의 정석", "헤드 퍼스트 디자인 패턴",
                "요즘 우아한 개발", "ORM JPA 프로그래밍", "Do it! Python!", "Unit Testing", "프로그래머의 뇌"
        };
        int[] prices = new int[]{
                12000, 20000, 24000, 30000, 10000,
                11000, 14000, 25000, 20000, 30000
        };
        int[] quantities = new int[]{
                1, 2, 3, 1, 2,
                2, 1, 4, 5, 1
        };

        for (int i = 0; i < titles.length; i++) {
            testBooks.add(Book.builder()
                    .title(titles[i])
                    .price(prices[i])
                    .quantity(quantities[i])
                    .build());
        }
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * 테스트 케이스 정리
     * [v] 도서 조회 : 정상
     * [v] 도서 구매 : 정상
     * [v] 도서 구매 : 예외 - 페이 잔액 부족
     * [v] 도서 구매 : 예외 - 책 재고 부족
     */

    @Test
    @DisplayName("도서 조회 : 정상")
    void givenBooksInDatabase_whenFindAllBooks_thenReturnAllBooksInDatabase() {
        // given (서비스 코드 메서드 내에 필요한 stub 정의)
        Mockito.when(bookRepository.findAll()).thenReturn(testBooks);

        // when (코드 실행 후 나온 결과 반환)
        List<Book> servicesBooks = bookService.findAllBooks();

        // then (결과 값과 테스트 코드에서 정의한 값이 같은지 확인)
        assertEquals(servicesBooks.size(), testBooks.size());
    }

    @Test
    @DisplayName("도서 구매 : 정상 - Map 형태 input")
    void givenOrderMap_whenBuyBooks_thenReturnReceipt() {
        // given (주문 Map 객체 생성)
        Map<Long, Integer> orderMap = new HashMap<>();
        orderMap.put(1L, 1);
        orderMap.put(2L, 1);
        orderMap.put(3L, 2);

        // 서비스 코드 메서드 내에 필요한 stub 정의
        Pay myPay1 = createPay(user, "My_Pay_1");
        Mockito.when(payRepository.findById(1L)).thenReturn(Optional.of(myPay1));
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(testBooks.get(0)));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(testBooks.get(1)));
        Mockito.when(bookRepository.findById(3L)).thenReturn(Optional.of(testBooks.get(2)));

        // 실제 가격 정의
        int sumOfActual = 80000;

        // when (10만원을 충전하고 이를 통해 구매를 진행하였을 때 총 구매한 금액 반환)
        myPay1.charge(100000);
        int sumOfPrice = bookService.buy(1L, orderMap);

        // then (결과 값의 금액과 실제 가격이 동일한지 확인)
        assertEquals(sumOfPrice, sumOfActual);
    }

    @Test
    @DisplayName("도서 구매 : 예외 - 페이 잔액 부족")
    void givenOrderMap_whenBuyBookWithNotEnoughCharge_thenThrowException() {
        // given (주문 Map 객체 생성)
        Map<Long, Integer> orderMap = new HashMap<>();
        orderMap.put(1L, 1);
//        orderMap.put(2L, 1);
//        orderMap.put(3L, 2);
        // -> 주석 제외하고 돌려보기

        Pay myPay1 = createPay(user, "My_Pay_1");
        Mockito.when(payRepository.findById(1L)).thenReturn(Optional.of(myPay1));
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(testBooks.get(0)));  // 첫 번째 책
//        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(testBooks.get(1)));
//        Mockito.when(bookRepository.findById(3L)).thenReturn(Optional.of(testBooks.get(2)));
        // -> 주석 제외하고 돌려보기
        // Exception 발생 : 이미 첫 번째 책에서 예외 처리 발생 -> 따라서 뒤에 것들은 필요 없는 것임 (불필요한 mock)

        // when (주문할 책보다 부족한 금액으로 충전)
        myPay1.charge(500);

        // then (주문할 책보다 현재 잔고가 부족하니 예외 발생)
        assertThrows(RuntimeException.class, () -> bookService.buy(1L, orderMap));
    }

    @Test
    @DisplayName("도서 구매 : 예외 - 책 재고 부족")
    void givenOrderMap_whenBookQuantityIsLack_thenThrowException() {
        // given (주문 Map 객체 생성)
        Map<Long, Integer> orderMap = new HashMap<>();
        orderMap.put(1L, 2);
//        orderMap.put(2L, 1);
//        orderMap.put(3L, 2);
        // -> 주석 제외하고 돌려보기
        // Exception 발생 : 이미 첫 번째 책에서 예외 처리 발생 -> 따라서 뒤에 것들은 필요 없는 것임 (불필요한 mock)

        // 페이 생성하고 충분한 금액으로 충전
        Pay myPay1 = createPay(user, "My_Pay_1");
        myPay1.charge(100000);

        // 서비스 코드 메서드 내에 필요한 stub 정의
        Mockito.when(payRepository.findById(1L)).thenReturn(Optional.of(myPay1));
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(testBooks.get(0)));

        // then (책의 재고가 부족한 경우 예외 발생)
        assertThrows(RuntimeException.class, () -> bookService.buy(1L, orderMap));
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