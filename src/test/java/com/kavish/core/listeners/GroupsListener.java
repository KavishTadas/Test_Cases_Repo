package com.kavish.core.listeners;

import com.kavish.core.annotations.FrameworkAnnotation;
import com.kavish.core.logging.Log;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupsListener implements IMethodInterceptor {

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods,
                                           ITestContext context) {

        // Build an immutable set of the requested groups (may be empty)
        Set<String> includedGroups =
                Set.copyOf(Arrays.asList(context.getIncludedGroups()));

        // If no group filter is active, run everything — no filtering needed
        if (includedGroups.isEmpty()) {
            Log.info("GroupsListener — no group filter active, running all "
                    + methods.size() + " method(s) in suite: " + context.getName());
            return methods;
        }

        Log.info("GroupsListener — active group filter: " + includedGroups
                + " | suite: " + context.getName());

        List<IMethodInstance> filtered = methods.stream()
                .filter(m -> {
                    Method method =
                            m.getMethod().getConstructorOrMethod().getMethod();
                    FrameworkAnnotation annotation =
                            method.getAnnotation(FrameworkAnnotation.class);

                    if (annotation == null) {
                        // FIX 3: log dropped methods so misconfigurations are visible
                        Log.warn("GroupsListener — EXCLUDED (no @FrameworkAnnotation): "
                                + method.getDeclaringClass().getSimpleName()
                                + "." + method.getName());
                        return false;
                    }

                    boolean matches = Arrays.stream(annotation.category())
                            .anyMatch(cat -> includedGroups.contains(cat.name()));

                    if (!matches) {
                        Log.info("GroupsListener — EXCLUDED (category mismatch): "
                                + method.getName()
                                + " | categories=" + Arrays.toString(annotation.category()));
                    }
                    return matches;
                })
                .collect(Collectors.toList());

        Log.info("GroupsListener — " + filtered.size() + " of " + methods.size()
                + " method(s) passed group filter " + includedGroups);

        return filtered;
    }
}