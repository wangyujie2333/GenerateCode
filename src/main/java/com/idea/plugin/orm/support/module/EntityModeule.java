package com.idea.plugin.orm.support.module;

import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.orm.support.TableModule;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityModeule extends TableModule {

    public EntityModeule(GeneratorContext context) {
        super(context);
    }

    public Set<String> getImports() {
        if (CollectionUtils.isEmpty(context.getTableInfoVO().getFieldInfos())) {
            return new HashSet<>();
        }
        return context.getTableInfoVO().getFieldInfos().stream().map(fieldInfoVO ->
                fieldInfoVO.getColumnType().getJclass().getName()).collect(Collectors.toSet());
    }

}
