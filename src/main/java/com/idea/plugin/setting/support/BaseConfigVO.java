package com.idea.plugin.setting.support;

import org.apache.commons.beanutils.BeanUtils;

public class BaseConfigVO {

    public <T extends BaseConfigVO> void copy(T config) {
        if (config == null) {
            return;
        }
        try {
            BeanUtils.copyProperties(this, config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
