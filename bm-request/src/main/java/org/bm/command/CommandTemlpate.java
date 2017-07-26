package org.bm.command;

import java.util.List;


import io.airlift.airline.Arguments;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;

/**
 *  命令模板
 * @author suntong
 *
 */
@Command(name=CommandTemlpate.NAME,description="A curl for the next-generation web.")
public class CommandTemlpate extends HelpOption{
    static final String NAME = "bm-request";
    static final int DEFAULT_TIMEOUT=-1;
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

    /**
     * 一次执行多个请求
     */
    @Option(name="-c",description="Number of multiple requests to perform at a time. Default is one request at a time.")
    public int concurrent=1;

    /**
     * 执行基准测试会话的请求数。默认是执行一个单一的请求
     */
    @Option(name="-n",description="Number of requests to perform for the benchmarking session. The default is to just perform a single request which usually leads to non-representative benchmarking results.")
    public int requests=1;
    
}
