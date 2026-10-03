package com.example.subscriptions.model;

import com.example.subscriptions.exception.SubscriptionException;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Абонемент клиента: тариф, дата начала, срок, заморозка и история операций.
 */
public class Subscription {

    private final String id;
    private Tariff tariff;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean frozen;
    private final List<String> history = new ArrayList<>();

    /**
     * Создать абонемент.
     *
     * @param id идентификатор (не пустой)
     * @param tariff тариф
     * @param start дата начала
     * @param period срок действия (положительный)
     */
    public Subscription(String id, Tariff tariff, LocalDate start, Period period) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id пустой");
        }
        if (period == null || period.isNegative() || period.isZero()) {
            throw new IllegalArgumentException("период должен быть > 0");
        }
        this.id = id;
        this.tariff = tariff;
        this.startDate = start;
        this.endDate = start.plus(period);
        this.frozen = false;
        history.add("Покупка за " + tariff.priceFor(period));
    }

    /** Идентификатор абонемента. */
    public String getId() { return id; }

    /** Тариф абонемента. */
    public Tariff getTariff() { return tariff; }

    /** Дата начала. */
    public LocalDate getStartDate() { return startDate; }

    /** Дата окончания. */
    public LocalDate getEndDate() { return endDate; }

    /** Заморожен ли абонемент. */
    public boolean isFrozen() { return frozen; }

    /** Копия истории операций (только для чтения). */
    public List<String> getHistory() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    /**
     * Продлить абонемент на заданный период.
     *
     * @param period срок продления
     * @return стоимость продления
     */
    public int extend(Period period) {
        if (period == null || period.isNegative() || period.isZero()) {
            throw new IllegalArgumentException("период должен быть > 0");
        }
        int price = tariff.priceFor(period);
        endDate = endDate.plus(period);
        history.add("Продление за " + price);
        return price;
    }

    /**
     * Заморозить абонемент на N дней со сдвигом даты окончания.
     *
     * @param days количество дней (> 0)
     */
    public void freeze(int days) {
        if (days <= 0) throw new IllegalArgumentException("дни > 0");
        if (frozen) throw new SubscriptionException("Уже заморожен");
        frozen = true;
        endDate = endDate.plusDays(days);
        history.add("Заморозка на " + days);
    }

    /** Разморозить абонемент. */
    public void unfreeze() {
        if (!frozen) throw new SubscriptionException("Не заморожен");
        frozen = false;
        history.add("Разморозка");
    }

    /**
     * Действует ли абонемент на заданную дату.
     *
     * @param date проверяемая дата
     * @return true, если не заморожен и дата входит в срок
     */
    public boolean isActiveOn(LocalDate date) {
        if (frozen) return false;
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Использовать абонемент на заданную дату.
     *
     * @param date дата использования
     * @throws SubscriptionException если заморожен, просрочен или ещё не начался
     */
    public void use(LocalDate date) {
        if (frozen) throw new SubscriptionException("Заморожен");
        if (date.isBefore(startDate)) throw new SubscriptionException("Ещё не начался");
        if (date.isAfter(endDate)) throw new SubscriptionException("Просрочен");
        history.add("Использование " + date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription s)) return false;
        return id.equals(s.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}