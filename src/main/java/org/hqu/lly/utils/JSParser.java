package org.hqu.lly.utils;

import javafx.application.Platform;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.*;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.component.MessagePopup;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;

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
        nashorn.eval("load('" + ResLoc.RANDOM_UTIL + "')");
        graalCtx.eval("js", "1");
        Source source = Source.newBuilder("js", new File(ResLoc.RANDOM_UTIL.getPath())).build();
        graalCtx.eval(source);
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
        return MethodTimer.measureExecutionTime(() -> evalScript(script));
    }

    /**
     * 默认的执行js脚本,js引擎为 GRAAL
     *
     * @param script 要执行的js脚本
     * @return 执行结果
     * @date 2023-07-11 19:45
     */
    public static Object evalScript(String script) {
        return evalScript(EngineType.GRAAL, script);
    }

    /**
     * 执行js脚本
     *
     * @param engineType js引擎类型,为nashorn 或 graal
     * @param script     要执行的js脚本
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
        Object result = null;
        try {
            result = graalCtx.eval("js", script);
        } catch (PolyglotException e) {
            SourceSection sl = e.getSourceLocation();
            String msg = e.getMessage() +"\nerror in: " + sl.getStartLine() + ':' + sl.getStartColumn() +" - "+ sl.getEndLine() + ':' + sl.getEndColumn();
            // TODO 分离ui逻辑
            Platform.runLater(() -> {
                new MessagePopup(msg).showPopup();
            });
        }
        return result;
    }

}
