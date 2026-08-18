package lk.icbt.dentalclinic.service.pattern;

import java.util.concurrent.atomic.AtomicLong;

/**
 * SINGLETON PATTERN.
 * <p>
 * Guarantees a single, shared, thread-safe source of truth for the next appointment
 * number across the whole application, so two receptionists registering appointments
 * at the same time can never be issued the same appointment number. A private
 * constructor plus a static accessor prevents any other class from constructing a
 * second, independent counter, which would defeat the purpose.
 * <p>
 * Implemented as a classic (non-Spring-managed) singleton, deliberately, to demonstrate
 * the Gang-of-Four pattern explicitly rather than relying on the fact that Spring beans
 * are singleton-scoped by default. {@link #initialise(long)} is called once at
 * application start-up (see {@code lk.icbt.dentalclinic.config.DataSeeder}) with the
 * count of appointments already persisted, so numbering survives an application
 * restart.
 */
public final class AppointmentNumberGenerator {

    private static final AppointmentNumberGenerator INSTANCE = new AppointmentNumberGenerator();

    private static final String PREFIX = "APT-";

    private final AtomicLong sequence = new AtomicLong(0);
    private volatile boolean initialised = false;

    private AppointmentNumberGenerator() {
        // private: the only way to obtain this object is getInstance()
    }

    public static AppointmentNumberGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * Seeds the counter from persisted state. Safe to call more than once during
     * tests; only the first call takes effect so a running counter is never rewound.
     */
    public synchronized void initialise(long existingCount) {
        if (!initialised) {
            sequence.set(existingCount);
            initialised = true;
        }
    }

    /** Returns the next unique appointment number, e.g. APT-000001, APT-000002 ... */
    public String nextAppointmentNumber() {
        long next = sequence.incrementAndGet();
        return PREFIX + String.format("%06d", next);
    }

    /** Test-only hook to reset singleton state between unit tests. */
    public synchronized void resetForTesting() {
        sequence.set(0);
        initialised = false;
    }
}
