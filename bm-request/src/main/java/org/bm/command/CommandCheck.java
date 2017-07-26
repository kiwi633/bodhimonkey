package org.bm.command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bm.enums.MessageEnums;
import org.bm.util.StringUtils;

public class CommandCheck {
    public static String checkCommand(CommandTemlpate commandTemlpate) {
        if (StringUtils.isEmpty(commandTemlpate.url.trim())) {
            return MessageEnums.NOT_URL.value();
        }

        if (checkUrl(commandTemlpate.url.trim())) {
            return MessageEnums.NOT_URL.value();
        }

        return commandTemlpate.url.trim();
    }

    /**
     * 校验url的正确性
     * 
     * @return
     */
    public static boolean checkUrl(String url) {
        String regex = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://|[fF][tT][pP]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(url).matches()) {
            return false;
        }
        return true;
    }

    /**
     * 校验域名的正确性
     * 
     * @param url
     * @return
     */
    public static boolean checkHOST(String url) {
        Pattern p = Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        matcher.find();
        try {
            InetAddress.getAllByName(matcher.group());
        } catch (UnknownHostException e) {
            System.out.println(MessageEnums.NOT_HOST.value() + url);
            return false;
        }
        return true;
    }

}
