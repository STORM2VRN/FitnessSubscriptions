package com.example.subscriptions.model;

import com.example.subscriptions.exception.SubscriptionException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Клиент с набором абонементов.
 */
public class Client {

    private final String id;
    private final String name;
    private final List<Subscription> subscriptions = new ArrayList<>();

    /**
     * Создать клиента.
     *
     * @param id идентификатор (не пустой)
     * @param name имя (не пустое)
     */
    public Client(String id, String name) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id пустой");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("имя пустое");
        this.id = id;
        this.name = name;
    }

    /** Идентификатор клиента. */
    public String getId() { return id; }

    /** Имя клиента. */
    public String getName() { return name; }

    /** Копия списка абонементов (только для чтения). */
    public List<Subscription> getSubscriptions() {
        return Collections.unmodifiableList(new ArrayList<>(subscriptions));
    }

    /**
     * Добавить абонемент клиенту.
     *
     * @param s абонемент
     * @throws SubscriptionException если абонемент с таким id уже есть
     */
    public void addSubscription(Subscription s) {
        for (var existing : subscriptions) {
            if (existing.getId().equals(s.getId())) {
                throw new SubscriptionException("id " + s.getId() + " уже есть");
            }
        }
        subscriptions.add(s);
    }

    /**
     * Найти действующий абонемент на заданную дату.
     *
     * @param date проверяемая дата
     * @return активный абонемент
     * @throws SubscriptionException если действующего нет
     */
    public Subscription activeSubscriptionOn(LocalDate date) {
        for (var s : subscriptions) {
            if (s.isActiveOn(date)) return s;
        }
        throw new SubscriptionException("Нет активного на " + date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client c)) return false;
        return id.equals(c.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}