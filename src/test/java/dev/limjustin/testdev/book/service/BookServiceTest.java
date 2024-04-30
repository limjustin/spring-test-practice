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
     * 1. [v] 책 조회 : 정상
     * 2. [v] 책 구매 : 정상
     * 3. [v] 책 구매 : 예외 - 페이 잔액 부족
     * 4. [v] 책 구매 : 예외 - 책 재고 부족
     */

    @Test
    @DisplayName("책 조회 : 정상")
    void givenBooksInDatabase_whenFindAllBooks_thenReturnAllBooksInDatabase() {
        // given
        Mockito.when(bookRepository.findAll()).thenReturn(testBooks);

        // when
        List<Book> servicesBooks = bookService.findAllBooks();

        // then
        assertEquals(servicesBooks.size(), testBooks.size());
    }

    @Test
    @DisplayName("책 구매 : 정상 - Map 형태 input")
    void givenOrderMap_whenBuyBooks_thenReturnReceipt() {
        // given
        Pay myPay1 = createPay(user, "My_Pay_1");

        Map<Long, Integer> orderMap = new HashMap<>();
        orderMap.put(1L, 1);
        orderMap.put(2L, 1);
        orderMap.put(3L, 2);

        Mockito.when(payRepository.findById(1L)).thenReturn(Optional.of(myPay1));
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(testBooks.get(0)));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(testBooks.get(1)));
        Mockito.when(bookRepository.findById(3L)).thenReturn(Optional.of(testBooks.get(2)));

        int sumOfActual = 80000;

        // when
        myPay1.charge(100000);
        int sumOfPrice = bookService.buy(1L, orderMap);

        // then
        assertEquals(sumOfPrice, sumOfActual);
    }

    @Test
    @DisplayName("책 구매 : 예외 - 페이 잔액 부족")
    void given_when_thenThrowException() {
        // given
        Pay myPay1 = createPay(user, "My_Pay_1");

        Map<Long, Integer> orderMap = new HashMap<>();
        orderMap.put(1L, 1);
        // orderMap.put(2L, 1);
        // orderMap.put(3L, 2);
        // -> 이유 : 이미 첫 번째에 걸렸으니까 뒤에 것들은 필요 없는 것임 (불필요한 mock)

        Mockito.when(payRepository.findById(1L)).thenReturn(Optional.of(myPay1));
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(testBooks.get(0)));
        // Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(testBooks.get(1)));
        // Mockito.when(bookRepository.findById(3L)).thenReturn(Optional.of(testBooks.get(2)));
        // -> 이유 : 이미 첫 번째에 걸렸으니까 뒤에 것들은 필요 없는 것임 (불필요한 mock)

        // when
        myPay1.charge(500);

        // then
        assertThrows(RuntimeException.class, () -> bookService.buy(1L, orderMap));
    }

    @Test
    @DisplayName("책 구매 : 예외 - 책 재고 부족")
    void given_when__thenThrowException() {
        // given
        Pay myPay1 = createPay(user, "My_Pay_1");
        myPay1.charge(100000);

        Map<Long, Integer> orderMap = new HashMap<>();
        orderMap.put(1L, 2);
        // orderMap.put(2L, 1);
        // orderMap.put(3L, 2);
        // -> 이유 : 이미 첫 번째에 걸렸으니까 뒤에 것들은 필요 없는 것임 (불필요한 mock)

        Mockito.when(payRepository.findById(1L)).thenReturn(Optional.of(myPay1));
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(testBooks.get(0)));

        // then
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