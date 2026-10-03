package com.example.subscriptions.model;

import java.time.Period;

/**
 * Тариф абонемента. Каждая константа сама вычисляет стоимость периода.
 */
public enum Tariff {

    ONE_TIME("Разовый") {
        @Override
        public int priceFor(Period period) {
            return 500 * period.getDays();
        }

        @Override
        public Period defaultPeriod() {
            return Period.ofDays(1);
        }
    },

    MONTHLY("Месячный") {
        @Override
        public int priceFor(Period period) {
            return 3000 * period.getMonths() + 500 * period.getDays();
        }

        @Override
        public Period defaultPeriod() {
            return Period.ofMonths(1);
        }
    },

    YEARLY("Годовой") {
        @Override
        public int priceFor(Period period) {
            return 30000 * period.getYears()
                    + 3000 * period.getMonths()
                    + 500 * period.getDays();
        }

        @Override
        public Period defaultPeriod() {
            return Period.ofYears(1);
        }
    },

    STUDENT("Студенческий") {
        @Override
        public int priceFor(Period period) {
            return (int) Math.round(MONTHLY.priceFor(period) * 0.6);
        }

        @Override
        public Period defaultPeriod() {
            return Period.ofMonths(1);
        }
    };

    private final String title;

    Tariff(String title) {
        this.title = title;
    }

    /** Название тарифа. */
    public String getTitle() {
        return title;
    }

    /**
     * Стоимость периода по тарифу.
     *
     * @param period отрезок времени
     * @return стоимость в рублях
     */
    public abstract int priceFor(Period period);

    /** Срок по умолчанию при покупке. */
    public abstract Period defaultPeriod();
}