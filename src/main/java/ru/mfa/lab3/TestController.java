package ru.mfa.lab3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // Первый эндпоинт: возвращает текст
    @GetMapping("/text")
    public String getText() {
        return "Привет, я Абдулкадыров Шамиль группа БКС2401";
    }

    // Второй эндпоинт: возвращает число
    @GetMapping("/number")
    public int getNumber() {
        return 24068;
    }
}