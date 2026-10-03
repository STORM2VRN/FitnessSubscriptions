package com.example.subscriptions.exception;

/**
 * Исключение при некорректных операциях с абонементом.
 */
public class SubscriptionException extends RuntimeException {

    /**
     * Создать исключение с сообщением.
     *
     * @param message описание ошибки
     */
    public SubscriptionException(String message) {
        super(message);
    }
}