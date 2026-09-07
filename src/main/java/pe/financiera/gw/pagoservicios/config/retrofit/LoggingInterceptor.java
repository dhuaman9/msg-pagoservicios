package pe.financiera.gw.pagoservicios.config.retrofit;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import java.io.IOException;

@Slf4j
public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        log.info(String.format("REST request %s, method: %s", request.url(),
            request.method()));
        log.info(String.format("REST request %s, body: %s", request.url(),
            getBodyAsString(request.body())));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();

        String responseString = response.body().string();
        log.info(String.format("JSON response %s", responseString));

        log.info(String.format("REST response %s in %.1fms, status: %d",
            response.request().url(), (t2 - t1) / 1e6d, response.code()));

        return response.newBuilder()
            .body(ResponseBody.create(response.body().contentType(), responseString)).build();
    }
    private String getBodyAsString(RequestBody body) {
        if (body != null) {
            Buffer buffer = new Buffer();
            try {
                body.writeTo(buffer);
            } catch (IOException e) {
                log.error("Error reading request body.", e);
            } finally {
                buffer.close();
            }
            return buffer.readUtf8();
        }
        return "empty";
    }

}
