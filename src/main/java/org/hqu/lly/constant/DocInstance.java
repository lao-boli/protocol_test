package org.hqu.lly.constant;

import org.hqu.lly.domain.component.HelpDocDialog;
import org.hqu.lly.utils.UIUtil;

/**
 * <p>
 * doc instances
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/8/11 21:01
 */
public class DocInstance {

    public static HelpDocDialog js = new HelpDocDialog(UIUtil.getPrimaryStage());

    private static final String jsTip;

    private static final String jsLibFun;

    static {
        jsTip = """
                JS执行时间应小于发送间隔, 可先执行几次JS脚本进行预热, 以减少后续执行时间。
                
                js引擎分为nashorn和graal。
                
                nashorn引擎仅支持es5语法,但在本应用程序中脚本执行速度比graal快。
                graal支持es6语法,但在本应用程序中脚本执行速度比nashorn慢。
                请跟据需求选择对应的js引擎。
                
                脚本生成的数据应以 return 返回。
                i.e. return Math.random();
                """;
        jsLibFun = """
                内置库函数
                                           
                随机数据生成函数
                                           
                生成一个指定范围内随机整数
                @param 随机数下界
                @param 随机数上界
                @param 整数进制, 默认为10进制
                randInt(low, high, radix)
                                           
                生成一个指定范围内随机浮点数
                @param 随机数下界
                @param 随机数上界
                randDouble(low, high)
                                           
                从输入数组中返回一个随机的数组元素
                randArr(arr)
                                           
                全局变量相关函数
                                           
                有时需要保证随机生成的数据中的某些数据保存有序增长,可使用全局变量进行状态的保存和修改。
                注意:
                1.全局变量在应用程序的所有面板中共享,js引擎间不共享, 故请保证每一个面板的全局变量的名称唯一性以及在切换到新的js引擎时重新创建变量.
                2.使用方法为先在脚本中调用newGV,点击测试脚本,创建一个全局变量, 随后注释掉newGV这一行调用,进行真正的业务测试.
                                           
                创建一个全局变量，默认值为0
                @param varName 全局变量名称
                @param initValue 初始值
                newGV(varName,initValue)
                                           
                删除一个全局变量
                @param varName 全局变量名称
                delGV(varName)
                                           
                若全局变量为数值类型，可使用本函数进行增长
                @param varName 全局变量名称
                @param increment 增长值
                incGV(varName,increment)
                                           
                若全局变量为数值类型，可使用本函数进行减少
                @param varName 全局变量名称
                @param decrement 减少值
                decGV(varName,decrement)
                            """;
        js.addText(jsLibFun);
        js.addText(jsTip);
    }

    public static HelpDocDialog getJs() {
        return js;
    }

}
