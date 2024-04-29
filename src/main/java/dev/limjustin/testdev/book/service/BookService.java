package dev.limjustin.testdev.book.service;

import dev.limjustin.testdev.book.domain.Book;
import dev.limjustin.testdev.book.domain.BookRepository;
import dev.limjustin.testdev.pay.domain.Pay;
import dev.limjustin.testdev.pay.domain.PayRepository;
import dev.limjustin.testdev.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final PayRepository payRepository;

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public int buy(Long payId, Map<Long, Integer> orderMap) {

        Pay account = payRepository.findById(payId)
                .orElseThrow(() -> new RuntimeException("Pay not found"));

        int sumPrice = 0;

        for (Long id : orderMap.keySet()) {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            int localSumPrice = book.getPrice() * orderMap.get(id);

            if (book.getQuantity() - orderMap.get(id) < 0)
                throw new RuntimeException("No Quantity available");

            if (localSumPrice > account.getBalance())  // 부분일 때도 발견할 수 있도록 함 -> 어차피 전체 때도 걸리니까
                throw new RuntimeException("Lack of balance");

            sumPrice += localSumPrice;
            book.minusQuantity(orderMap.get(id));
        }

        if (sumPrice > account.getBalance())
            throw new RuntimeException("Lack of balance");

        account.pay(sumPrice);
        return sumPrice;
    }
}
