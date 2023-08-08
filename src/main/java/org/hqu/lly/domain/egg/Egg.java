package org.hqu.lly.domain.egg;

import lombok.val;
import org.hqu.lly.domain.component.MessagePopup;

import java.util.Random;

public class Egg {

    private static final String[] hqu = {"欢迎报考华侨大学!","会通中外 并育德才"};
    private static final String[] hqugxy = {"欢迎报考华侨大学工学院物联网工程!","崇德尚信 求是创新"};

    public static void egg(String msg) {
        if ("华侨大学".equals(msg)) {
            new MessagePopup(MessagePopup.Type.INFO, randomText(hqu)).showPopup(30,0.8);
        }
        if ("华侨大学工学院".equals(msg)) {
            new MessagePopup(MessagePopup.Type.INFO, randomText(hqugxy)).showPopup(30,0.8);
        }
    }

    private static String randomText(String[] arr) {
        val index = new Random().nextInt(arr.length);
        return arr[index];
    }

}
