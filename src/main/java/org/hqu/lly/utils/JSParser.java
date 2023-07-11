package org.hqu.lly.utils;

import javafx.application.Platform;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.hqu.lly.domain.component.MessagePopup;

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

    /**
     * js引擎预热,减少首次执行脚本时间
     */
    @SneakyThrows
    public static void preheat() {
        nashorn.eval("1");
        graalCtx.eval("js", "1");
    }


    /**
     * 测试js脚本
     *
     * @param engineType js执行引擎类型
     * @param script     要执行的js脚本
     * @return 执行时间和结果
     * @date 2023-07-11 19:45
     */
    public static MethodTimer.ResultWithTime<Object> testScript(EngineType engineType, String script) {
        return MethodTimer.measureExecutionTime(() -> evalScript(engineType, script));
    }

    /**
     * 默认的测试js脚本,js引擎为 NASHORN
     *
     * @param script 要执行的js脚本
     * @return 执行时间和结果
     * @date 2023-07-11 19:45
     */
    public static MethodTimer.ResultWithTime<Object> testScript(String script) {
        return MethodTimer.measureExecutionTime(() -> evalScript(EngineType.NASHORN, script));
    }

    /**
     * 默认的执行js脚本,js引擎为 NASHORN
     *
     * @param script 要执行的js脚本
     * @return 执行结果
     * @date 2023-07-11 19:45
     */
    public static Object evalScript(String script) {
        return evalScript(EngineType.NASHORN, script);
    }

    /**
     * 执行js脚本
     *
     * @param engineType js引擎类型,为nashorn 或 graal
     * @param script 要执行的js脚本
     * @return 执行结果
     * @date 2023-07-11 19:45
     */
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
        Object result = null;
        try {
            result = nashorn.eval(script);
        } catch (ScriptException e) {
            // TODO 分离ui逻辑
            Platform.runLater(() -> {
                new MessagePopup(e.getMessage()).showPopup();
            });
        }
        return result;
    }

    public static Object graalEval(String script) {
        return graalCtx.eval("js", script);
    }

}
