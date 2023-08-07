package org.hqu.lly.domain.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hqu.lly.enums.ConfigType;

import java.util.List;

/**
 * <p>
 * client session config
 * <p>
 *
 * @author hqully
 * @version 1.0
 * @date 2023/6/29 20:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClientSessionConfig extends SessionConfig{

    String serverAddr;

    List<String> addrHistoryList;

    public ClientSessionConfig() {
        super();
        type = ConfigType.CLIENT;
    }

    public String toString() {
        return this.id;

    }

}
