package main.model.cache;

import java.net.URI;
import java.util.Optional;
import main.config.RedisConfig;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
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

    URI redisUri = URI.create(redisConfig.getUrl());

    Config config = new Config();
    SingleServerConfig serverConfig = config.useSingleServer()
        .setAddress("redis://" + redisUri.getHost() + ":" + redisUri.getPort())
        .setConnectionPoolSize(10)
        .setConnectionMinimumIdleSize(10)
        .setTimeout(5000);

    if (redisUri.getUserInfo() != null) {
      serverConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(":")+1));
    }

    try {
      redisson = Redisson.create(config);
    } catch (RedisConnectionException Exc) {
      System.out.println("Failed connect to Redis");
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
