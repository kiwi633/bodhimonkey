package org.bm.enums;

/**
 * 信息提示
 * @author suntong
 *
 */
public enum MessageEnums {
    NOT_URL("curl: (6) Could not resolve host:"),
    NOT_COMMAND("tkylcurl: try 'tkylcurl -h' or 'tkylcurl --help' for more information");
    private String value;
    
    private MessageEnums(String v){
        this.value = v;
    }
    
    public String value(){
        return this.value;
    }

}
