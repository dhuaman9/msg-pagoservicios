package pe.financiera.gw.pagoservicios.config.retrofit;


import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class RestClientConfig {

    public OkHttpClient getHttpClient(int maxRequest, long connectTimeout,
                                      long readTimeout, long writeTimeout) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(maxRequest);

        return new OkHttpClient.Builder()
            .dispatcher(dispatcher).connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .addInterceptor(new LoggingInterceptor())
            .addInterceptor(new pe.financiera.framework.common.retrofit.LoggingInterceptor())
            .hostnameVerifier((s, sslSession) -> true).build();

    }

}
