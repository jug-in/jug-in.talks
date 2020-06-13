import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class Caching {

    /**
     * Simulates a cache stampede using the different algorithms.
     */
    static void stampede() throws Exception {
        //Locking cut = new Locking();
        //Simple cut = new Simple();
        Probabilistic cut = new Probabilistic();
        Key k1 = new Key("1");
        ExecutorService exec = Executors.newCachedThreadPool();
        List<Callable<Value>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Callable<Value> callable = () -> {
                Value v = cut.fetch(k1);
                System.out.println("Fetched value " + v);
                return v;
            };
            tasks.add(callable);
        }
        exec.invokeAll(tasks);
        exec.shutdown();
    }

    /**
     * Generates statistics for the probabilistic function as used in the graphs.
     * 
     * @param beta
     * @param delta
     * @param runs
     */
    static void statistics(double beta, Duration delta, long runs) {
        Probabilistic probabilistic = new Probabilistic();
        List<Long> fetchEarlyValues = new ArrayList<>();
        for (int i = 0; i < runs; i++) {
            fetchEarlyValues.add(-probabilistic.fetchEarly(beta, delta));
        }
        // Map of Seconds, Count
        Map<Long, Long> groupedFetchEarlyValues = fetchEarlyValues.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<Long> sortedSeconds = groupedFetchEarlyValues.keySet().stream().sorted().collect(Collectors.toList());
        Map<Long, Long> cumulativeGroupedFetchEarlyValues = new LinkedHashMap<>();
        long sum = 0L;
        for (long second : sortedSeconds) {
            long count = groupedFetchEarlyValues.get(second);
            sum += count;
            cumulativeGroupedFetchEarlyValues.put(second, sum);
        }
        cumulativeGroupedFetchEarlyValues.forEach((k, v) -> System.out.println(k + "," + v));
    }

    public static void main(String[] args) throws Exception {
        //stampede();
        statistics(1, Duration.ofSeconds(40), 1_000_000);
    }

    static class Key {
        private final String key;
        public Key(String key) {
            this.key = key;
        }
        @Override
        public String toString() {
            return key;
        }
    }
    static class Value {
        private final String value;
        public Value(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return value;
        }
    }
    static class Item {
        Value value;
        Instant expiresAt;
        Duration lastFetchDuration;
        Item(Value value, Instant expiresAt, Duration lastFetchDuration) {
            this.value = value;
            this.expiresAt = expiresAt;
            this.lastFetchDuration = lastFetchDuration;
        }
    }
    static class Cache {
        private Map<Key, Value> valueStore = new ConcurrentHashMap<>();
        private Map<Key, Item> itemStore = new ConcurrentHashMap<>();
        public Value getValue(Key key) {
            System.out.println("Getting key " + key);
            return valueStore.get(key);
        }
        public void setValue(Key key, Value value) {
            System.out.println("Setting key " + key);
            valueStore.put(key, value);
        }
        public Item getItem(Key key) {
            System.out.println("Getting item " + key);
            return itemStore.get(key);
        }
        public void setItem(Key key, Item item) {
            System.out.println("Setting item " + key);
            itemStore.put(key, item);
        }
    }
    
    public static Value recompute(Key key) {
        System.out.println("Recomputing key " + key);
        try {
            // Simulate expensive recalculation.
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        return new Value(key.toString());
    }

    static class Simple {
        private Cache cache = new Cache();
        // tag::fetch[]
        public Value fetch(Key key) {
            Value cachedValue = cache.getValue(key);
            if (cachedValue == null) {
                cachedValue = recompute(key);
                cache.setValue(key, cachedValue);
            }
            return cachedValue;
        }
        // end::fetch[]
    }

    static class Locking {
        private Cache cache = new Cache();
        // tag::locking[]
        private Map<Key, CompletableFuture<Value>> pendingMap = new ConcurrentHashMap<>();
        
        public Value fetch(Key key) throws Exception {
            Value cachedValue = cache.getValue(key);
            if (cachedValue != null) {
                return cachedValue;
            }
            CompletableFuture<Value> deferred = pendingMap.computeIfAbsent(key,
                k -> CompletableFuture.supplyAsync(() -> {
                    Value value = recompute(k);
                    cache.setValue(k, value);
                    pendingMap.remove(k);
                    return value;
                }));
            return deferred.get();
        }
        // end::locking[]
    }

    static class External {
        private Cache cache = new Cache();
        // tag::external[]
        public Value fetch(Key key) {
            return cache.getValue(key);
        }
        // end::external[]
    }

    static class Probabilistic {
        private Cache cache = new Cache();
        private long ttlSeconds;
        // tag::xfetch[]
        private double beta;
        
        public Value fetch(Key key) {
            Item cachedItem = cache.getItem(key);
            Instant now = Instant.now();
            if (cachedItem == null
                    || now.plusSeconds(fetchEarly(beta, cachedItem.lastFetchDuration))
                          .isAfter(cachedItem.expiresAt)) {
                Value value = recompute(key);
                Duration fetchDuration = Duration.between(Instant.now(), now);
                Instant expiresAt = now.plusSeconds(ttlSeconds);
                cachedItem = new Item(value, expiresAt, fetchDuration);
                cache.setItem(key, cachedItem);
            }
            return cachedItem.value;
        }

        public long fetchEarly(double beta, Duration delta) {
            return (long) (delta.toSeconds() * beta * -Math.log(Math.random()));
        }
        // end::xfetch[]
    }
}
