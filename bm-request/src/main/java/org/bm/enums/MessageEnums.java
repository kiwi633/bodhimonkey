package org.bm.enums;

/**
 * 信息提示
 * 
 * @author suntong
 *
 */
public enum MessageEnums {
    /**
     * 无法解析端口
     */
    NOT_HOST("bm-request: (6) Could not resolve host:"),
    /**
     * 错误的URL地址
     */
    ERROR_URL("bm-request: (7) This URL cannot be parsed"),

    /**
     * 没有指定URL地址
     */
    NOT_URL("bm-request: no URL specified!"),

    /**
     * 没有指定命令选项
     */
    NOT_COMMAND("bm-request: try 'bm-request -h' or 'bm-request --help' for more information");
    private String value;

    private MessageEnums(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

}
