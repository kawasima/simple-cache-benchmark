package net.unit8.benchmark.cache;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kawasima
 */
public class CacheTest {

    private static final Random random = new Random();
    private static final long TOTAL_COUNT = 10000000L;
    private static final int THREAD_COUNT = 2;
    @Test
    public void test() throws InterruptedException {
        Cache<String, Object>[] managers = new Cache[]{
                new SynchronizedHashMapCache<String, Object>(),
                new ConcurrentHashMapCache<String, Object>(),
                CacheBuilder.newBuilder().build(),
                CacheBuilder.newBuilder().initialCapacity(65536).build(),
                CacheBuilder.newBuilder().initialCapacity(65536)
                        .concurrencyLevel(64).build(),
        };

        for (Cache<String, Object> manager : managers) {
            runThreads(manager);
        }
    }

    private void runThreads(final Cache<String, Object> manager) throws InterruptedException {
        final AtomicLong tryCount = new AtomicLong(0);
        final AtomicLong missCount = new AtomicLong(0);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        long t1 = System.currentTimeMillis();
        for (int i=0; i < THREAD_COUNT; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < (TOTAL_COUNT/THREAD_COUNT); j++) {
                        final byte[] buffer = new byte[2];
                        random.nextBytes(buffer);
                        String random = BaseEncoding.base64Url().omitPadding().encode(buffer);
                        tryCount.incrementAndGet();
                        if (manager.getIfPresent(random) == null) {
                            HashFunction hashing = Hashing.sha512();
                            manager.put(random, hashing.hashString(random, Charsets.UTF_8));
                            missCount.incrementAndGet();
                        }
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);
        long t2 = System.currentTimeMillis();
        System.out.println(manager.getClass().getSimpleName()
                + ":" + ((t2-t1))
                + " " + missCount.get() + "/" + tryCount.get());
    }
}
