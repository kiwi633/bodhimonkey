package com.taikang.tkylcurl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.bm.command.CommandTemlpate;
import org.junit.Test;

import io.airlift.airline.SingleCommand;

public class MainTest {
    @Test
    public void simple(){
        //        Request request = fromArgs("http://www.baidu.com").createRequest();
        //        assertEquals("GET", request.method());
        //        assertEquals("http://www.baidu.com/", request.url().toString());
        //        assertNull(request.body());
    }

    @Test
    public void dataPost(){
        //        Request request = fromArgs("-d","foo","http://example.com").createRequest();
        //        RequestBody body = request.body();
        //        assertEquals("POST", request.method());
        //        assertEquals("foo", bodyAsString(body));
    }

    @Test
    public void commandTem(){
        CommandTemlpate  a = SingleCommand.singleCommand(CommandTemlpate.class).parse("-X","hello X");
        System.out.println(a.method);
    }
    //    private static String bodyAsString(RequestBody body){
    //        try {
    //            Buffer buffer = new Buffer();
    //            body.writeTo(buffer);
    //            return buffer.readString(body.contentType().charset());
    //        } catch (IOException e) {
    //            throw new RuntimeException();
    //        }
    //    }

    @Test
    public void executorService(){
        ExecutorService ex = Executors.newFixedThreadPool(4);
        ex.shutdown();
    }

    @Test
    public void regexUrl(){
        String url = "https://www.baidu.com";
        String regex = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://|[fF][tT][pP]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(url).matches()) {
            System.out.println("非法网址");
        }
    }
    
    @Test
    public void yuming(){
        try {
            InetAddress[] a = InetAddress.getAllByName("csdn.com");
            System.out.println(a[0].getHostAddress());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
