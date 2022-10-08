package com.hitol.rpc.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rpc.config")
@Data
public class ConfigProperties {

}
