package com.taikang.tkylcurl;

import org.bm.command.CommandTemlpate;
import org.junit.Test;

import io.airlift.airline.SingleCommand;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

import static org.bm.Main.fromArgs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
/**
 * Unit test for simple App.
 */
public class MainTest {
    @Test
    public void simple(){
        Request request = fromArgs("http://www.baidu.com").createRequest();
        assertEquals("GET", request.method());
        assertEquals("http://www.baidu.com/", request.url().toString());
        assertNull(request.body());
    }
    
    @Test
    public void dataPost(){
        Request request = fromArgs("-d","foo","http://example.com").createRequest();
        RequestBody body = request.body();
        assertEquals("POST", request.method());
        assertEquals("foo", bodyAsString(body));
    }
    
    @Test
    public void commandTem(){
        CommandTemlpate  a = SingleCommand.singleCommand(CommandTemlpate.class).parse("-X","hello X");
        System.out.println(a.method);
    }
    private static String bodyAsString(RequestBody body){
        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readString(body.contentType().charset());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
    
}
