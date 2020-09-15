package main.model.cache;

import java.util.Optional;
import main.config.RedisConfig;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;
import org.springframework.stereotype.Service;

@Service
public class RedisCache {

  private RedissonClient redisson;
  private RMapCache<String, Integer> rMapCache;
  private RKeys rKeys;

  private final static String KEY = "LOGINNED_USERS";
  private RedisConfig redisConfig;


  public RedisCache(RedisConfig redisConfig) {
    this.redisConfig = redisConfig;
    init();
  }

  private void init() {
    Config config = new Config();
    config.useSingleServer().setUsername("h").setPassword("p0a1805a79d9f6dae64b32265792aff24adddd7375fb85698280b03a29710fca5")
        .setAddress("redis://ec2-52-16-131-126.eu-west-1.compute.amazonaws.com:12019")
        .setConnectionPoolSize(10)
        .setConnectionMinimumIdleSize(10)
        .setTimeout(5000);
    try {
      redisson = Redisson.create(config);
    } catch (RedisConnectionException Exc) {
      System.out.println("Не удалось подключиться к Redis");
      System.out.println(Exc.getMessage());
    }
    rKeys = redisson.getKeys();
    rMapCache = redisson.getMapCache(KEY);
    rMapCache.clear();
  }

  public void saveSessionToCache(String sessionId, int idUser) {
    rMapCache.put(sessionId, idUser);
  }

  public boolean isCacheSession(String sessionId) {
    return rMapCache.containsKey(sessionId);
  }

  public void deleteSessionFromCache(String sessionId) {
    if (isCacheSession(sessionId)) {
      rMapCache.remove(sessionId);
    }
  }

  public Optional<Integer> findUserIdBySessionId(String sessionId) {
    if (rMapCache.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(rMapCache.getOrDefault(sessionId, null));
  }

}
