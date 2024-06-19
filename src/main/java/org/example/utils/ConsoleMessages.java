package org.example.utils;

public class ConsoleMessages {
    private ConsoleMessages() {
    }

    public static final String INPUT_LAST_NAME = "Введите фамилию: ";
    public static final String INPUT_FIRST_NAME = "Введите имя: ";
    public static final String INPUT_EMAIL = "Введите email: ";
    public static final String INPUT_CORRECT_EMAIL = "Введите корректный email: ";
    public static final String AVAILABLE_ROLES = "Доступные роли:";
    public static final String INPUT_ROLE_NUMBER = "Введите номер выбранной роли: ";
    public static final String INPUT_CORRECT_ROLE_NUMBER = "Введите номер выбранное роли из доступных: ";

    public static final String REGISTRATION = """
            Регистрация кандидата:
            \tФамилия: %s
            \tИмя: %s
            \tEmail: %s
            \tРоль: %s""";
}
