    package com.example.subscriptions;

    import com.example.subscriptions.exception.SubscriptionException;
    import com.example.subscriptions.model.Client;
    import com.example.subscriptions.model.Subscription;
    import com.example.subscriptions.model.Tariff;
    import org.junit.jupiter.api.Test;

    import java.time.LocalDate;
    import java.time.Period;

    import static org.junit.jupiter.api.Assertions.*;

    class SubscriptionTest {

        private static final LocalDate START = LocalDate.of(2025, 1, 1);

        private Subscription sub() {
            return new Subscription("S1", Tariff.MONTHLY, START, Period.ofMonths(1));
        }

        @Test
        void создаётся_нормально() {
            var s = sub();
            assertEquals(START, s.getStartDate());
            assertEquals(START.plusMonths(1), s.getEndDate());
            assertFalse(s.isFrozen());
        }

        @Test
        void пустой_id_падает() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Subscription("  ", Tariff.MONTHLY, START, Period.ofMonths(1)));
        }

        @Test
        void нулевой_период_падает() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Subscription("S1", Tariff.MONTHLY, START, Period.ZERO));
        }

        @Test
        void активен_внутри() {
            assertTrue(sub().isActiveOn(START.plusDays(10)));
        }

        @Test
        void активен_в_последний_день() {
            var s = sub();
            assertTrue(s.isActiveOn(s.getEndDate()));
        }

        @Test
        void не_активен_после() {
            var s = sub();
            assertFalse(s.isActiveOn(s.getEndDate().plusDays(1)));
        }

        @Test
        void использовать_просроченный_падает() {
            var s = sub();
            assertThrows(SubscriptionException.class,
                    () -> s.use(s.getEndDate().plusDays(1)));
        }

        @Test
        void использовать_до_начала_падает() {
            var s = sub();
            assertThrows(SubscriptionException.class,
                    () -> s.use(START.minusDays(1)));
        }

        @Test
        void заморозка_сдвигает_дату() {
            var s = sub();
            var old = s.getEndDate();
            s.freeze(10);
            assertEquals(old.plusDays(10), s.getEndDate());
            assertTrue(s.isFrozen());
        }

        @Test
        void использовать_замороженный_падает() {
            var s = sub();
            s.freeze(5);
            assertThrows(SubscriptionException.class, () -> s.use(START.plusDays(2)));
        }

        @Test
        void двойная_заморозка_падает() {
            var s = sub();
            s.freeze(5);
            assertThrows(SubscriptionException.class, () -> s.freeze(3));
        }

        @Test
        void разморозка_незамороженного_падает() {
            assertThrows(SubscriptionException.class, () -> sub().unfreeze());
        }

        @Test
        void продление_сдвигает_дату() {
            var s = sub();
            var old = s.getEndDate();
            int price = s.extend(Period.ofMonths(1));
            assertEquals(old.plusMonths(1), s.getEndDate());
            assertTrue(price > 0);
        }

        @Test
        void отрицательная_заморозка_падает() {
            assertThrows(IllegalArgumentException.class, () -> sub().freeze(-5));
        }

        @Test
        void equals_по_id() {
            var a = new Subscription("X", Tariff.MONTHLY, START, Period.ofMonths(1));
            var b = new Subscription("X", Tariff.YEARLY, START.plusDays(3), Period.ofYears(1));
            var c = new Subscription("Y", Tariff.MONTHLY, START, Period.ofMonths(1));
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, c);
        }

        @Test
        void история_не_течёт() {
            var s = sub();
            assertThrows(UnsupportedOperationException.class, () -> s.getHistory().clear());
            assertFalse(s.getHistory().isEmpty());
        }

        @Test
        void дубликат_абонемента_у_клиента_падает() {
            var c = new Client("C1", "Test");
            c.addSubscription(new Subscription("S1", Tariff.MONTHLY, START, Period.ofMonths(1)));
            assertThrows(SubscriptionException.class,
                    () -> c.addSubscription(new Subscription("S1", Tariff.YEARLY, START, Period.ofYears(1))));
        }

        @Test
        void нет_активного_у_клиента_падает() {
            var c = new Client("C1", "Test");
            c.addSubscription(new Subscription("S1", Tariff.MONTHLY, START, Period.ofMonths(1)));
            assertThrows(SubscriptionException.class,
                    () -> c.activeSubscriptionOn(START.plusYears(5)));
        }

        @Test
        void студенческий_дешевле_месячного() {
            var p = Period.ofMonths(1);
            assertTrue(Tariff.STUDENT.priceFor(p) < Tariff.MONTHLY.priceFor(p));
        }
    }