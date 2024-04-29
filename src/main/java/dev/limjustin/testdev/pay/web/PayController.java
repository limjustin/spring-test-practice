package dev.limjustin.testdev.pay.web;

import dev.limjustin.testdev.pay.service.PayService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class PayController {

    private final PayService payService;
}
