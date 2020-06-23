package nil.nadph.qnotified.util;

public class UserFlagConst {
    public static final int BF_REJECT = 1;
    public static final int BF_SILENT_GONE = 1 << 1;
    public static final int BF_SILENT_NO_LOAD = 1 << 2;
    public static final int BF_HIDE_INFO = 1 << 3;
    public static final int BF_TAMPER_BATCH_LONG_MSG = 1 << 4;
    public static final int BF_TAMPER_STARTUP_RANDOM = 1 << 5;
    public static final int BF_TAMPER_LIFECYCLE = 1 << 6;

    public static final int BF_FUNC_STICKY = 1 << 30;

    public static final int WF_BYPASS_AUTH_2 = 1 << 3;

    public static final int WF_FUNC_STICKY = 1 << 30;
}
