package icar.a5i4s.com.smartbox.helper;

/**
 * Created by light on 2016/11/9.
 */

public class Act {
    //菜单入口控制
    public static final int TAG_MENU_TO_CONSOLE = 0;
    public static final String URI_CHECK = "check";
    public static final String URI_PUT = "put";
    public static final String URI_GET = "get";
    public static final String URI_LEND = "lend";
    public static final String URI_CHANGE = "change";
    public static final String URI_MENU = "menu";

    //图片数组
    public static final int ARR_LOGOUT = 0;
    public static final int ARR_MENU = 1;
    public static final int ARR_PUT = 2;
    public static final int ARR_CHANGE = 3;
    public static final int ARR_LEND = 4;
    public static final int ARR_GET = 5;
    public static final int ARR_CHECK = 6;

    //行动代号
    public static final int ACTION_RETURN_MENU = 1;
    public static final int ACTION_SUCCESS = 2;

    public static final int ACTION_ADAPTER_PUT = 11;
    public static final int ACTION_ADAPTER_GET = 12;
    public static final int ACTION_ADAPTER_CHANGE = 13;
    public static final int ACTION_ADAPTER_CANCEL_CHANGE = 14;
    public static final int ACTION_ADAPTER_LEND = 15;
    public static final int ACTION_ADAPTER_CANCEL_LEND = 16;



    //true 未推出
    //false 已推出
    public static boolean isPush = true;

    //操作码
    public static final String openDoor = "55aa0101";
    public static final String closeDoor = "55aa0102";
    public static final String pushOut = "55aa0105";
    public static final String pushIn = "55aa0106";
    public static final String reset = "55aa0103";
    public static final String turn = "55aa0104";

    //操作回执
    public static final String openHz = "55AA03000100";
    public static final String closeHz = "55AA03000200";
    public static final String pushOutHz = "55AA03000500";
    public static final String pushInHz = "55AA03000600";
    public static final String turnHz = "55AA03000400";

    public static String getBoxCode() {
        return "nidaye";
    }

}
