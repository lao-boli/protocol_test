package org.hqu.lly.utils;

import javafx.application.Platform;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.*;
import org.hqu.lly.constant.ResLoc;
import org.hqu.lly.domain.component.MessagePopup;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Slf4j
public class JSParser {

   public enum EngineType {
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
        nashorn.eval("load('" + ResLoc.GLOBE_VARIABLE + "')");
        graalCtx.eval("js", "1");
        Source randomUtil = Source.newBuilder("js", ResLoc.RANDOM_UTIL).build();
        Source globeVariable = Source.newBuilder("js", ResLoc.GLOBE_VARIABLE).build();
        graalCtx.eval(randomUtil);
        graalCtx.eval(globeVariable);
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
        String s = "(function() {" + script + "})();";
        Object result = null;
        try {
            result = nashorn.eval(s);
        } catch (ScriptException e) {
            // TODO 分离ui逻辑
            Platform.runLater(() -> {
                new MessagePopup(MessagePopup.Type.ERROR, e.getMessage()).showPopup();
            });
        }
        // nashornPrint();
        return result;
    }

    private static void nashornPrint() {
        String[] variableNames = nashorn.getContext().getBindings(ScriptContext.ENGINE_SCOPE).keySet().toArray(new String[0]);
        // 打印变量名
        for (String name : variableNames) {
            System.out.println(name);
        }
    }

    public static Object graalEval(String script) {
        Object result = null;
        String s = "(function() {" + script + "})();";
        try {
            result = graalCtx.eval("js", s);
        } catch (PolyglotException e) {
            SourceSection sl = e.getSourceLocation();
            String msg = e.getMessage() + "\nerror in: " + sl.getStartLine() + ':' + sl.getStartColumn() + " - " + sl.getEndLine() + ':' + sl.getEndColumn();
            // TODO 分离ui逻辑
            Platform.runLater(() -> {
                new MessagePopup(MessagePopup.Type.ERROR,msg).showPopup();
            });
        }

        // graalPrint();
        return result;
    }

    private static void graalPrint() {
        Value bindings = graalCtx.getBindings("js");
        // 获取所有存在的变量和函数
        for (String key : bindings.getMemberKeys()) {
            System.out.println(key);
        }
    }

}
