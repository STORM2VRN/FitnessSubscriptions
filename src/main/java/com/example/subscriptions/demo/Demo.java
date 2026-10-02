package com.example.subscriptions.demo;

import com.example.subscriptions.exception.SubscriptionException;
import com.example.subscriptions.model.Client;
import com.example.subscriptions.model.Tariff;
import com.example.subscriptions.model.Subscription;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Demo {

    public static void main(String[] args) {
        List<Client> clients = new ArrayList<>();
        String[] names = {"Иван", "Анна", "Олег", "Маша", "Дима",
                "Лена", "Серёга", "Оля", "Паша", "Юля"};
        Tariff[] tariffs = Tariff.values();
        LocalDate today = LocalDate.of(2025, 1, 15);

        for (int i = 0; i < names.length; i++) {
            Client c = new Client("C" + (i + 1), names[i]);
            clients.add(c);
            for (int j = 0; j < 3; j++) {
                Tariff t = tariffs[(i + j) % tariffs.length];
                c.addSubscription(new Subscription(
                        "S" + (i + 1) + "-" + (j + 1),
                        t,
                        today.minusDays(i * 5L),
                        t.defaultPeriod()));
            }
        }

        System.out.println("Клиентов: " + clients.size()
                + ", абонементов: " + clients.stream()
                .mapToInt(x -> x.getSubscriptions().size()).sum() + "\n");

        var s = clients.get(0).getSubscriptions().get(1);
        System.out.println("Абонемент: " + s.getId() + ", тариф: " + s.getTariff().getTitle());
        System.out.println("Стоимость: " + s.getTariff().priceFor(s.getTariff().defaultPeriod()));
        System.out.println("Работает на +10 дней? " + s.isActiveOn(today.plusDays(10)));

        try {
            s.use(today.plusDays(5));
            System.out.println("Использовали " + s.getId());
        } catch (SubscriptionException e) {
            System.out.println("Не удалось использовать: " + e.getMessage());
        }

        var expired = clients.get(1).getSubscriptions().get(0);
        try {
            expired.use(expired.getEndDate().plusDays(1));
        } catch (SubscriptionException e) {
            System.out.println("Просрочен: " + e.getMessage());
        }

        var before = s.getEndDate();
        s.freeze(7);
        System.out.println("После заморозки endDate=" + s.getEndDate() + " (было " + before + ")");

        try {
            s.use(today.plusDays(5));
        } catch (SubscriptionException e) {
            System.out.println("Заморожен: " + e.getMessage());
        }

        s.unfreeze();
        System.out.println("Разморожен, frozen=" + s.isFrozen());

        int price = s.extend(Period.ofMonths(1));
        System.out.println("Продление за " + price + ", новый endDate=" + s.getEndDate());

        try {
            var active = clients.get(0).activeSubscriptionOn(today.plusDays(2));
            System.out.println("Активный: " + active.getId());
        } catch (SubscriptionException e) {
            System.out.println("Нет активного: " + e.getMessage());
        }

        System.out.println("\nИстория " + s.getId() + ":");
        s.getHistory().forEach(h -> System.out.println("  " + h));

        System.out.println("\nГотово");
    }}