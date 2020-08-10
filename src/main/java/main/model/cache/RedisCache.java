package main.model.cache;

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
    config.useSingleServer()
        .setAddress("redis://" + redisConfig.getHost() + ":" + redisConfig.getPort());
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

  public Integer findUserIdBySessionId(String sessionId) {
    return rMapCache.get(sessionId);
  }

}