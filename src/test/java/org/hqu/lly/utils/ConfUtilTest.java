package org.hqu.lly.utils;

import org.hqu.lly.domain.config.ConfigSummary;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

/**
 * <p>
 *
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/8/19 19:25
 */
class ConfUtilTest {

    @Test
    void saveConf() {
    }

    @Test
    void load() throws FileNotFoundException {
        ConfigSummary x = ConfUtil.load();
        System.out.println(x);
    }

}
