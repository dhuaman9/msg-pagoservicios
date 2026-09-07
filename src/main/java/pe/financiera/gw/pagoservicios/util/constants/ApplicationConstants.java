package pe.financiera.gw.pagoservicios.util.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationConstants {

    public static final String ASYNC_THREAD_POOL_TASK_EXECUTOR = "threadPoolTaskExecutor";

    public static final String TTL_TIMESTAMP = "ttl_timestamp";

    public static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");
}
