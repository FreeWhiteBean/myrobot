package simbot.demo.utils;

import java.util.ArrayList;
import java.util.Arrays;

public final class Const {

    /**
     * <B>构造方法</B><BR>
     */
    private Const() {
    }

    //群聊监听列表
    public static final ArrayList<String> GROUP_LIST = new ArrayList<>(Arrays.asList("788015046"));//呱
//    public static final ArrayList<String> GROUP_LIST = new ArrayList<>(Arrays.asList("982232171"));//测试
//    public static final ArrayList<String> GROUP_LIST = new ArrayList<>(Arrays.asList("971838349"));//黑大铁

    //私聊监听列表
    public static final ArrayList<String> PRIVATE_LIST = new ArrayList<>(Arrays.asList("1210453237", "854961517", "704685927"));

    public static final String QH = "https://chp.shadiao.app/api.php?from=cherishcyt";
    public static final String PYQ = "https://pyq.shadiao.app/api.php?from=cherishcyt";
    public static final String DJT = "https://du.shadiao.app/api.php?from=cherishcyt";
    public static final String ZUICHOU = "https://nmsl.shadiao.app/api.php?level=min&from=xxx";
    public static final String BAISHU = "https://api.ixiaowai.cn/mcapi/mcapi.php?return=json";
    public static final String HAOKANG = "https://api.ixiaowai.cn/api/api.php?return=json";
    public static final String HAOKANG1 = "https://api.lolicon.app/setu?apikey=403781315fb5f58d4da325&r18=1";
//    public static final String HAOKANG1 = "https://api.lolicon.app/setu?proxy=125.124.19.36:12365/htserver&apikey=403781315fb5f58d4da325&r18=2";
//    public static final String HAOKANG = "https://api.52hyjs.com/api/shipaitu/api.php?code=json";

    /** 宏接口*/
    public static final String MACRO_URL = "https://server.jx3box.com/post/list?type=macro&per=20&subtype=%s&search=%s&page=1";
    /** 历史上的今天*/
    public static final String HIS_TODAY = "https://api.66mz8.com/api/today.php?format=json";
    public static final String HIS_TODAY_REDIS_KEY = "his_today:%s";

    /** 自动回话*/
    public static final String AI_API = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=%s";

    public static final Integer DONG_NUM = 5;
}
