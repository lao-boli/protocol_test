package org.hqu.lly.icon;

/**
 * <p>
 * close icon
 * <p>
 *
 * @author hqully
 * @version 1.0
 *  @date 2023-07-18 10:12
 */
public class CloseIcon extends BaseIcon {

    public CloseIcon() {
        this(true);
    }
    public CloseIcon(boolean bold) {
        if (bold){
            setStyleClass("icon-close-bold");
        }else {
            setStyleClass("icon-close");
        }
    }

    public CloseIcon(String color) {
        super(color);
        setStyleClass("icon-close-bold");
    }

}
