package org.bm.enums;

/**
 * 信息提示
 * @author suntong
 *
 */
public enum MessageEnums {
    NOT_HOST("bm-request: (6) Could not resolve host:"),
    NOT_URL("bm-request: no URL specified!"),
    NOT_COMMAND("bm-request: try 'bm-request -h' or 'bm-request --help' for more information");
    private String value;
    
    private MessageEnums(String v){
        this.value = v;
    }
    
    public String value(){
        return this.value;
    }

}
