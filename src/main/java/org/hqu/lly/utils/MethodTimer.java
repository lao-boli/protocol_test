package org.hqu.lly.utils;

import java.util.function.Supplier;

public class MethodTimer {

    public static <T> ResultWithTime<T> measureExecutionTime(Supplier<T> function) {
        long startTime = System.currentTimeMillis();

        // 执行函数
        T result = function.get();

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        return new ResultWithTime<>(result, elapsedTime);
    }


    // 结果和执行时间的封装类
    public static class ResultWithTime<T> {

        private final T result;
        private final long time;

        public ResultWithTime(T result, long time) {
            this.result = result;
            this.time = time;
        }

        public T getResult() {
            return result;
        }

        public long getTime() {
            return time;
        }

    }

}
