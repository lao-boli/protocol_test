package org.hqu.lly.domain.component;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.hqu.lly.domain.config.JSCodeConfig;
import org.hqu.lly.domain.config.StoringAreaConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    /**
     *
     * @param onChoose see: {@link StagingArea#onChoose}
     */
    public JSStagingArea(Consumer<String> onChoose) {
        super();
        stagingArea = new StagingArea(onChoose);
        VBox vBox = new VBox(stagingArea.tabPane);
        // do not delete
        vBox.setStyle("-fx-background-color:#3c3f41");
        content = vBox;
        pane.setCenter(content);
    }

    @Override
    protected void initTitleBar() {
        initTitleBar("JS脚本暂存区");
    }


    public void saveConfig(JSCodeConfig config){
        ArrayList<StoringAreaConfig> configs = new ArrayList<>();
        stagingArea.tabPane.getTabs().forEach(tab -> {
            String title = ((TitleTab) tab).getTabTitleField().getText();
            String text = ((StagingArea.Content) tab.getContent()).textArea.getText();
            configs.add(new StoringAreaConfig(title,text));
        });
        config.setStoringAreaConfigs(configs);
    }

    public void loadConfig(JSCodeConfig config){
        List<StoringAreaConfig> storingAreaConfigs = config.getStoringAreaConfigs();
        if (storingAreaConfigs != null){
            stagingArea.loadConfig(storingAreaConfigs);
        }
    }

}
