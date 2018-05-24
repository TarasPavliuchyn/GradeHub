package io.gradehub.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller uses only for checking that application works without (authentication is not needed)
 * @author ptar
 * @since 1.0
 */
@RestController
public class PingController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from GradeHub!";
    }

}
