package lk.icbt.dentalclinic.service.pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TDD note: this test was written first to pin down the exact contract of the
 * Singleton (single shared instance, zero-padded APT-###### format, no duplicate
 * numbers even under concurrent access) before AppointmentNumberGenerator's
 * thread-safety approach (AtomicLong) was chosen.
 */
class AppointmentNumberGeneratorTest {

    @BeforeEach
    void resetSingleton() {
        AppointmentNumberGenerator.getInstance().resetForTesting();
    }

    @Test
    void getInstance_alwaysReturnsTheSameObject() {
        AppointmentNumberGenerator first = AppointmentNumberGenerator.getInstance();
        AppointmentNumberGenerator second = AppointmentNumberGenerator.getInstance();

        assertThat(first).isSameAs(second);
    }

    @Test
    void nextAppointmentNumber_isSequentialAndZeroPadded() {
        AppointmentNumberGenerator generator = AppointmentNumberGenerator.getInstance();

        assertThat(generator.nextAppointmentNumber()).isEqualTo("APT-000001");
        assertThat(generator.nextAppointmentNumber()).isEqualTo("APT-000002");
        assertThat(generator.nextAppointmentNumber()).isEqualTo("APT-000003");
    }

    @Test
    void initialise_seedsCounterFromExistingData_butOnlyOnce() {
        AppointmentNumberGenerator generator = AppointmentNumberGenerator.getInstance();

        generator.initialise(100);
        assertThat(generator.nextAppointmentNumber()).isEqualTo("APT-000101");

        generator.initialise(999); // second call must be ignored
        assertThat(generator.nextAppointmentNumber()).isEqualTo("APT-000102");
    }

    @Test
    void nextAppointmentNumber_neverProducesADuplicate_underConcurrentAccess() throws InterruptedException {
        AppointmentNumberGenerator generator = AppointmentNumberGenerator.getInstance();
        int threads = 20;
        int perThread = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        Set<String> numbers = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                for (int j = 0; j < perThread; j++) {
                    numbers.add(generator.nextAppointmentNumber());
                }
                latch.countDown();
            });
        }

        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
        pool.shutdown();
        assertThat(numbers).hasSize(threads * perThread);
    }
}
