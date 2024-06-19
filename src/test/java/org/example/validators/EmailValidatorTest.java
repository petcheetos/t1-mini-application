package org.example.validators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailValidatorTest {

    @CsvSource({
        "email@email.com, true",
        "email12@email.ru, true",
        "email-12email.ru, false",
        "email-12email, false",
        "@email.ru, false",
    })
    @ParameterizedTest
    void testIsSubsequence(String email, boolean expected) {
        assertEquals(expected, EmailValidator.isValidEmail(email));
    }
}
