package com.example.subscriptions.model;

import com.example.subscriptions.exception.SubscriptionException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Client {

    private final String id;
    private final String name;
    private final List<Subscription> subscriptions = new ArrayList<>();

    public Client(String id, String name) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id пустой");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("имя пустое");
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public List<Subscription> getSubscriptions() {
        return Collections.unmodifiableList(new ArrayList<>(subscriptions));
    }

    public void addSubscription(Subscription s) {
        for (var existing : subscriptions) {
            if (existing.getId().equals(s.getId())) {
                throw new SubscriptionException("id " + s.getId() + " уже есть");
            }
        }
        subscriptions.add(s);
    }

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