package org.bm;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.bm.command.CommandTemlpate;

import com.google.common.base.Joiner;

import io.airlift.airline.SingleCommand;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.http2.Http2;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;

/**
 * 启动
 * 
 * @author suntong
 * 
 */
public class CommandRun implements Runnable {
    static final String NAME = "bm-request";
    static final int DEFAULT_TIMEOUT = -1;
    private static Logger frameLogger;
    public static CommandTemlpate commandTemlpate;

    private OkHttpClient client;

    public static CommandTemlpate fromArgs(String... args) {
        return SingleCommand.singleCommand(CommandTemlpate.class).parse(args);
    }

    public CommandRun(CommandTemlpate commandTemlpate) {
        CommandRun.commandTemlpate = commandTemlpate;
    }

    private static String versionString() {
        try {
            Properties prop = new Properties();
            InputStream in = CommandRun.class.getResourceAsStream("/tkylcurl-version.properties");
            prop.load(in);
            in.close();
            return prop.getProperty("version");
        } catch (IOException e) {
            throw new AssertionError("Could not load okcurl-version.properties.");
        }
    }

    private static String protocols() {
        return Joiner.on(", ").join(Protocol.values());
    }

    private static void enableHttp2FrameLogging() {
        frameLogger = Logger.getLogger(Http2.class.getName());
        frameLogger.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        LogRecord record = new LogRecord(handler.getLevel(), handler.getErrorManager().toString());
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        simpleFormatter.format(record);
        handler.setFormatter(simpleFormatter);
    }

    public OkHttpClient createClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.followSslRedirects(commandTemlpate.followRedirects);
        if (commandTemlpate.connectTimeout != DEFAULT_TIMEOUT) {
            builder.connectTimeout(commandTemlpate.connectTimeout, TimeUnit.SECONDS);
        }
        if (commandTemlpate.readTimeout != DEFAULT_TIMEOUT) {
            builder.readTimeout(commandTemlpate.readTimeout, TimeUnit.SECONDS);
        }

        if (commandTemlpate.allowInsecured) {
            X509TrustManager trustManager = createInscureTrustManager();
            SSLSocketFactory sslSocketFactory = createInsecureSslSocketFactory(trustManager);
            builder.sslSocketFactory(sslSocketFactory, trustManager);
            builder.hostnameVerifier(createInsecureHostnameVerifier());
        }
        return builder.build();
    }

    private static HostnameVerifier createInsecureHostnameVerifier() {
        return new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private static X509TrustManager createInscureTrustManager() {
        return new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                //
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                //
            }
        };
    }

    private static SSLSocketFactory createInsecureSslSocketFactory(X509TrustManager trustManager) {
        try {
            SSLContext contenxt = SSLContext.getInstance("TLS");
            contenxt.init(null, new TrustManager[] { trustManager }, null);
            return contenxt.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public Request createRequest() {
        Request.Builder request = new Request.Builder();
        request.url(commandTemlpate.url);
        request.method(getRequestMethod(), getRequestBody());
        return request.build();
    }

    private String getRequestMethod() {
        if (commandTemlpate.method != null) {
            return commandTemlpate.method;
        }
        if (commandTemlpate.data != null) {
            return "POST";
        }
        return "GET";
    }

    private RequestBody getRequestBody() {
        if (commandTemlpate.data == null) {
            return null;
        }

        String mimeType = "application/x-www-form-urlencoded";

        if (commandTemlpate.headers != null) {
            for (String header : commandTemlpate.headers) {
                String[] parts = header.split(":", -1);
                if ("Content-Type".equalsIgnoreCase(parts[0])) {
                    mimeType = parts[1].trim();
                    commandTemlpate.headers.remove(header);
                    break;
                }
            }
        }
        return RequestBody.create(MediaType.parse(mimeType), commandTemlpate.data);
    }

    /**
     * 关闭所有持久性连接
     */
    private void close() {
        client.connectionPool().evictAll();
    }

    /**
     * 执行程序
     */
    public void run() {
        if (commandTemlpate.showHelpIfRequested()) {
            return;
        }

        if (commandTemlpate.version) {
            System.out.println(NAME + " " + versionString());
            System.out.println("protocols: " + protocols());
            return;
        }

        if (commandTemlpate.showHttp2Frames) {
            enableHttp2FrameLogging();
        }
        client = createClient();
        Request request = createRequest();
        try {
            Response response = client.newCall(request).execute();
            if (commandTemlpate.showHeaders) {
                System.out.println(StatusLine.get(response));
                Headers headers = request.headers();
                for (int i = 0, size = headers.size(); i < size; i++) {
                    System.out.println(headers.name(i) + ":" + headers.value(i));
                }
                System.out.println();
            }

            Sink out = Okio.sink(System.out);
            BufferedSource source = response.body().source();
            while (!source.exhausted()) {
                out.write(source.buffer(), source.buffer().size());
                out.flush();
            }
            response.body().close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }

}
