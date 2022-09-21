package com.idea.plugin.completion;

import com.intellij.codeInsight.completion.AddSpaceInsertHandler;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CodeCompletionContributor extends CompletionContributor {


    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        Set<LookupElement> keywords = getKeywords(parameters.getPosition());
        result.addAllElements(keywords);
    }

    private Set<LookupElement> getKeywords(PsiElement element) {
        PsiElement prev = PsiTreeUtil.prevLeaf(element);
        if (prev == null) {
            return new HashSet<>();
        }
        Set<LookupElement> set = new HashSet<>();
        set.add(createKeywordWithSpace("aaa"));
        set.add(createKeyword("bbb1"));
        set.add(createKeyword("bbb2"));
        set.add(createKeyword("bbb3"));
        set.add(createKeyword("bbb4"));
        return set;
    }

    private LookupElement createKeyword(String keyword) {
        return LookupElementBuilder.create(keyword).bold();
    }

    private LookupElement createKeywordWithSpace(String keyword) {
        return LookupElementBuilder.create(keyword).withInsertHandler(AddSpaceInsertHandler.INSTANCE_WITH_AUTO_POPUP).bold();
    }
}
