package org.hqu.lly.utils;

import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Slf4j
public class JSParser {

    enum EngineType {
        NASHORN,
        GRAAL
    }

    static ScriptEngineManager engineManager = new ScriptEngineManager();
    static ScriptEngine nashorn = engineManager.getEngineByName("nashorn");

    // region graal
    static Engine graal = Engine.newBuilder()
            .option("engine.WarnInterpreterOnly", "false")
            .build();
    static Context graalCtx = Context.newBuilder("js").engine(graal).build();

    // endregion
    static {
        try {
            nashorn.eval("1");
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        graalCtx.eval("js", "1");
    }

    public static Object evalScript(String script) {
        return evalScript(EngineType.NASHORN, script);
    }

    public static Object evalScript(EngineType engineType, String script) {
        if (engineType.equals(EngineType.NASHORN)) {
            return nashornEval(script);
        }
        if (engineType.equals(EngineType.GRAAL)) {
            return graalEval(script);
        }
        return null;
    }

    public static Object nashornEval(String script) {
        long begin = System.currentTimeMillis();
        Object result = null;
        try {
            result = nashorn.eval(script);
        } catch (ScriptException e) {
            log.warn(e.getMessage());

        }
        System.out.println(System.currentTimeMillis() - begin);
        System.out.println("返回结果: " + result);
        return result;
    }

    public static Object graalEval(String script) {
        long begin = System.currentTimeMillis();
        Object result = graalCtx.eval("js", script);
        System.out.println(System.currentTimeMillis() - begin);
        System.out.println("返回结果: " + result);
        return result;
    }


/*    public static void main(String[] args) throws ScriptException {
        String script = "function add(a, b) { return Math.random().toFixed(2); }() add(2, 3);1;";
        // String script = "function add(a, b) { return a+b; } add(2, 3);";
        for (int i = 0; i < 50; i++) {
            evalScript(script);
        }


        // 执行 JavaScript 代码，并获取返回结果
        nashorn.eval("1");
        graalCtx.eval("js", "1");
        long begin = System.currentTimeMillis();
        // Object result = graalCtx.eval("js", script);
        Object result = nashorn.eval(script);
        System.out.println(System.currentTimeMillis() - begin);

        System.out.println("返回结果: " + result);

    }*/

}
