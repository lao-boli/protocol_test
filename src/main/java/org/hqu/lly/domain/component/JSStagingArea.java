package org.hqu.lly.domain.component;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.hqu.lly.domain.config.JSCodeConfig;
import org.hqu.lly.domain.config.StoringAreaConfig;

import java.util.ArrayList;
import java.util.List;

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

    public JSCodeConfig jsCodeConfig;

    public JSStagingArea() {
        super();
        stagingArea = new StagingArea();
        VBox vBox = new VBox(stagingArea.root);
        vBox.setStyle("-fx-background-color:#3c3f41");
        content = vBox;
        pane.setCenter(content);
    }

    public JSStagingArea(JSCodeConfig config) {
        this();
        jsCodeConfig = config;
    }

    public void saveConfig(JSCodeConfig config){
        ArrayList<StoringAreaConfig> configs = new ArrayList<>();
        stagingArea.root.getTabs().forEach(tab -> {
            String title = ((TitleTab) tab).getTabTitleField().getText();
            String text = ((StagingArea.Content) tab.getContent()).textArea.getText();
            configs.add(new StoringAreaConfig(title,text));
        });
        config.setStoringAreaConfigs(configs);
    }
    public void saveConfig(){
        ArrayList<StoringAreaConfig> configs = new ArrayList<>();
        stagingArea.root.getTabs().forEach(tab -> {
            String title = ((TitleTab) tab).getTabTitleField().getText();
            String text = ((StagingArea.Content) tab.getContent()).textArea.getText();
            configs.add(new StoringAreaConfig(title,text));
        });
        jsCodeConfig.setStoringAreaConfigs(configs);
    }

    public void loadConfig(){
        loadConfig(jsCodeConfig);
    }

    public void loadConfig(JSCodeConfig config){
        List<StoringAreaConfig> storingAreaConfigs = config.getStoringAreaConfigs();
        if (storingAreaConfigs != null){
            stagingArea.loadConfig(storingAreaConfigs);
        }
    }

}
