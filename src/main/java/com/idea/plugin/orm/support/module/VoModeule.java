package com.idea.plugin.orm.support.module;

import com.idea.plugin.orm.support.GeneratorContext;
import com.idea.plugin.orm.support.TableModule;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class VoModeule extends TableModule {

    public VoModeule(GeneratorContext context) {
        super(context);
    }

    public Set<String> getImports() {
        if (CollectionUtils.isEmpty(context.getClazzInfoVO().getImportList())) {
            return new HashSet<>();
        }
        return context.getClazzInfoVO().getImportList().stream().filter(s -> !s.endsWith("VO")).collect(Collectors.toSet());
    }
}
