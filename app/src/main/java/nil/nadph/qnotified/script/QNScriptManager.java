package nil.nadph.qnotified.script;

import android.content.Intent;
import android.os.Build;
import android.os.FileUtils;
import android.view.View;
import android.widget.CompoundButton;
import androidx.annotation.RequiresApi;
import bsh.EvalError;
import bsh.Interpreter;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static nil.nadph.qnotified.util.Utils.*;

public class QNScriptManager {

    private static List<QNScript> scripts = new ArrayList<>();
    public static int enables = 0;
    public static boolean enableall = false;
    private static String scriptsPath;
    private static boolean init = false;


    /**
     * 添加一个脚本
     *
     * @param file 文件
     */
    public static void addScript(String file) throws Exception {
        if (isNullOrEmpty(file)) throw new RuntimeException("file is null");
        if (hasScript(file)) throw new RuntimeException("script exists");
        // to do
        // 操作: 将文件移动到软件数据文件夹下
        File s = new File(file);
        File dir = new File(scriptsPath);
        if (!dir.exists()) dir.mkdirs();
        File f = new File(dir, s.getName());
        Utils.copy(s, f);
        String code = readByReader(new FileReader(f));
        if (!isNullOrEmpty(code))
            scripts.add(execute(code));
    }

    public static void addEnable() {
        enables++;
        if (enables > scripts.size() - 1) enables = scripts.size();
    }

    public static void delEnable() {
        enables--;
        if (enables < 0) enables = 0;
    }

    /**
     * 判断脚本是否存在
     *
     * @param file 文件
     * @return 是否存在
     */
    public static boolean hasScript(String file) throws Exception {
        if (Utils.isNullOrEmpty(file)) return false;
        // to do
        // 判断文件
        try {
            QNScript qs = execute(Utils.readByReader(new FileReader(new File(file))));
            for (QNScript q : getScripts()) {
                if (qs.getLabel().equalsIgnoreCase(q.getLabel())) {
                    return true;
                }
            }
        } catch (EvalError e) {
            log(e);
            throw new RuntimeException("不是有效的java文件");
        }
        return false;
    }

    /**
     * 删除脚本
     *
     * @param file
     */
    public static void delScript(String file) {
        // to do
        // 删除文件
    }

    public static String error = "啥也没";

    /**
     * 获取所有的脚本代码
     *
     * @return
     */
    public static List<String> getScriptCodes() {
        // to do
        // 返回全部脚本代码
        List<String> codes = new ArrayList<String>() {{
            try {
                add(Utils.readByReader(new BufferedReader(new InputStreamReader(Utils.toInputStream("/assets/demo.java")))));
            } catch (IOException e) {
                log(e);
            }
        }};
        File dir = new File(scriptsPath);
        if (!dir.exists()) dir.mkdirs();
        if (!dir.isDirectory()) {
            log(new RuntimeException("脚本文件夹不应为一个文件"));
        }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) continue;
            try {
                String code = Utils.readByReader(new FileReader(f));
                if (!Utils.isNullOrEmpty(code))
                    codes.add(code);
            } catch (Exception e) {
                log(e);
            }
        }
        return codes;
    }

    /**
     * 获取所有的脚本
     *
     * @return
     */
    public static List<QNScript> getScripts() {
        return scripts;
    }

    public static void init() {
        if (init) return;
        scriptsPath = getApplication().getFilesDir().getAbsolutePath() + "/scripts/";
        for (String code : getScriptCodes()) {
            try {
                scripts.add(execute(code));
            } catch (EvalError e) {
                log(e);
            }
        }
        init = true;
    }

    public static QNScript execute(String code) throws EvalError {
        Interpreter lp = new Interpreter();
        lp.setClassLoader(Initiator.class.getClassLoader());
        QNScript qn = QNScript.create(lp, code);
        lp.eval(code);
        return qn;
    }


    public static void changeGlobal(CompoundButton compoundButton, boolean b) {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        cfg.putBoolean(ConfigItems.qn_script_global, b);
        try {
            cfg.save();
        } catch (IOException e) {
            log(e);
        }
    }

    public static void enableAll() {
        enableall = true;
        for (QNScript qs : QNScriptManager.getScripts())
            if (!qs.isEnable()) {
                qs.setEnable(true);
                addEnable();
            }

    }

    public static void disableAll() {
        enableall = false;
        for (QNScript qs : QNScriptManager.getScripts())
            if (qs.isEnable()) {
                qs.setEnable(false);
                delEnable();
            }

    }

    public static int getAllCount() {
        return scripts.size();
    }

    public static int getEnableCount() {
        return enables;
    }

    public static void enableAll(CompoundButton compoundButton, boolean b) {
        if (b) enableAll();
        else disableAll();
    }

    public static boolean isEnableAll() {
        return enableall;
    }
}
