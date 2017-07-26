package org.bm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bm.command.CommandTemlpate;
import org.bm.enums.MessageEnums;

import io.airlift.airline.SingleCommand;

public class Main1 {
    private static CommandTemlpate commandTemlpate ;
    public static CommandTemlpate fromArgs(String... args){
        return SingleCommand.singleCommand(CommandTemlpate.class).parse(args);
    }
    
    public static void main(String[] args) {
        commandTemlpate = fromArgs(args);
        ExecutorService pool = Executors.newFixedThreadPool(commandTemlpate.concurrent);
        if(args==null|| args.length==0){
            System.out.println(MessageEnums.NOT_COMMAND.value());
        }else{
            for (int i = 0; i < commandTemlpate.requests; i++) {
                pool.execute(new Main(commandTemlpate));
            }
        }
        
        pool.shutdown();
    }
}
