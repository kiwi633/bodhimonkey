package org.bm;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
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

import org.bm.enums.MessageEnums;

import com.google.common.base.Joiner;

import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
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

@Command(name=Main.NAME,description="A curl for the next-generation web.")
public class Main extends HelpOption implements Runnable{
    static final String NAME = "bm-request";
    static final int DEFAULT_TIMEOUT=-1;
    private static Logger frameLogger;

    /**
     * 指定要使用的命令
     */
    @Option(name={"-X","--request"},description="Specify request command to use")
    public String method;

    /**
     * 请求post数据
     */
    @Option(name={"-d","--data"},description="HTTP POST data")
    public String data;

    /**
     * 显示的版本并退出
     */
    @Option(name={"-V","--version"}, description="Show version number and quit")
    public boolean version;

    @Option(name={"--frames"},description="Log HTTP/2 frames to STDERR")
    public boolean showHttp2Frames;

    /**
     * 重定向
     */
    @Option(name = {"-L","--location"},description = "Follow redirects")
    public boolean followRedirects;

    /**
     * 连接时间的最大值
     */
    @Option(name = "--connect-timeout",description="Maximum time allowed for connection (seconds)")
    public int connectTimeout=DEFAULT_TIMEOUT;

    /**
     * 读取数据的最大值
     */
    @Option(name = "--read-timeout",description="Maximum time allowed for reading data (seconds)")
    public int readTimeout = DEFAULT_TIMEOUT;

    /**
     * 用户自定义header值
     */
    @Option(name={"-H","--header"},description="Custom header to pass to server")
    public List<String> headers;

    /**
     * 允许连接没有证书的SSL网站
     */
    @Option(name={"-K","--insecure"},description = "Allow connections to SSL sites without certs")
    public boolean allowInsecured;

    /**
     * 在输出中包含协议的头部信息
     */
    @Option(name={"-i","--include"},description="Include protocol headers in the output")
    public boolean showHeaders;

    /**
     * 连接远程路径
     */
    @Arguments(title="url",description="Remote resource URL")
    public String url;

    @Option(name="-c",description="Number of multiple requests to perform at a time. Default is one request at a time.")
    public int concurrent=1;

    @Option(name="-n",description="Number of requests to perform for the benchmarking session. The default is to just perform a single request which usually leads to non-representative benchmarking results.")
    public int requests=1;

    private OkHttpClient client;

    public static Main fromArgs(String... args){
        return SingleCommand.singleCommand(Main.class).parse(args);
    }

    public static void main( String[] args ){
        if(args==null|| args.length==0){
            System.out.println(MessageEnums.NOT_COMMAND.value());
        }else{
            fromArgs(args).run();
        }

    }

    private static String versionString(){
        try {
            Properties prop = new Properties();
            InputStream in = Main.class.getResourceAsStream("/tkylcurl-version.properties");
            prop.load(in);
            in.close();
            return prop.getProperty("version");
        } catch (IOException e) {
            throw new AssertionError("Could not load okcurl-version.properties.");
        }
    }

    private static String protocols(){
        return Joiner.on(", ").join(Protocol.values());
    }

    private static void enableHttp2FrameLogging(){
        frameLogger = Logger.getLogger(Http2.class.getName());
        frameLogger.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        LogRecord record = new LogRecord(handler.getLevel(), handler.getErrorManager().toString());
        SimpleFormatter simpleFormatter=new SimpleFormatter();
        simpleFormatter.format(record);
        handler.setFormatter(simpleFormatter);
    }

    public OkHttpClient createClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.followSslRedirects(followRedirects);
        if(connectTimeout!=DEFAULT_TIMEOUT){
            builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        }
        if(readTimeout!= DEFAULT_TIMEOUT){
            builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        }

        if(allowInsecured){
            X509TrustManager trustManager = createInscureTrustManager();
            SSLSocketFactory sslSocketFactory = createInsecureSslSocketFactory(trustManager);
            builder.sslSocketFactory(sslSocketFactory, trustManager);
            builder.hostnameVerifier(createInsecureHostnameVerifier());
        }
        return builder.build();
    }

    private static HostnameVerifier createInsecureHostnameVerifier(){
        return new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private static X509TrustManager createInscureTrustManager(){
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

    private static SSLSocketFactory createInsecureSslSocketFactory(X509TrustManager trustManager){
        try {
            SSLContext contenxt = SSLContext.getInstance("TLS");
            contenxt.init(null, new TrustManager[] {trustManager}, null);
            return contenxt.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        } 
    }

    public Request createRequest(){
        Request.Builder request = new Request.Builder();
        request.url(url);
        request.method(getRequestMethod(), getRequestBody());
        return request.build();
    }
    private String getRequestMethod(){
        if(method!=null){
            return method;
        }
        if(data!=null){
            return "POST";
        }
        return "GET";
    }

    private RequestBody getRequestBody(){
        if(data == null){
            return null;
        }

        String mimeType = "application/x-www-form-urlencoded";

        if(headers != null){
            for (String header : headers) {
                String[] parts = header.split(":",-1);
                if("Content-Type".equalsIgnoreCase(parts[0])){
                    mimeType = parts[1].trim();
                    headers.remove(header);
                    break;
                }
            }
        }
        return RequestBody.create(MediaType.parse(mimeType), data);
    }

    /**
     * 关闭所有持久性连接
     */
    private void close(){
        client.connectionPool().evictAll();
    }

    public void run() {
        if(this.showHelpIfRequested()){
            return;
        }

        if(version){
            System.out.println(NAME + " "+ versionString());
            System.out.println("protocols: "+ protocols());
            return;
        }

        if(showHttp2Frames){
            enableHttp2FrameLogging();
        }
        client = createClient();
        Request request = createRequest();
        try {
            Response response = client.newCall(request).execute();
            if(showHeaders){
                System.out.println(StatusLine.get(response));
                Headers headers = request.headers();
                for (int i = 0, size = headers.size();i<size;i++) {
                    System.out.println(headers.name(i)+":"+headers.value(i));
                }
                System.out.println();
            }

            Sink out = Okio.sink(System.out);
            BufferedSource source = response.body().source();
            while(!source.exhausted()){
                out.write(source.buffer(), source.buffer().size());
                out.flush();
            }
            response.body().close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            close();
        }

    }

}
