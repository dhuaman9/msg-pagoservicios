package pe.financiera.gw.pagoservicios.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.financiera.framework.pubsub.messaging.ProcessingResult;

/**
 * Redis configuration
 */
@Configuration
public class RedissonConfig {
    private final static String HOST_PORT_FORMAT = "redis://%s:%d";
    private final static String LOCK_MAP = "LOCK_MAP";

    @Value("${redis.host}")
    private String address;
    @Value("${redis.port}")
    private Integer portNumber;
    @Value("${appName}")
    private String applicationName;

    private Config initConfig() {
        Config config = new Config();
        String redisAddress = String.format(HOST_PORT_FORMAT, address, portNumber);
        config.useSingleServer()
            .setAddress(redisAddress);
        return config;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = initConfig();
        return Redisson.create(config);
    }

    @Bean
    public RMapCache<String, ProcessingResult> lockMap(RedissonClient redissonClient) {
        return redissonClient.getMapCache(LOCK_MAP.concat(applicationName));
    }
}
