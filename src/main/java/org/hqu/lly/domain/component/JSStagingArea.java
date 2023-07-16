package org.hqu.lly.domain.component;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * <p>
 * js code 暂存区dialog
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/7/15 20:54
 */
public class JSStagingArea extends MyDialog<Node>{

    public final StagingArea stagingArea;

    public JSStagingArea() {
        super();
        stagingArea = new StagingArea();
        VBox vBox = new VBox(stagingArea.root);
        vBox.setStyle("-fx-background-color:#3c3f41");
        content = vBox;
        pane.setCenter(content);
    }

}
