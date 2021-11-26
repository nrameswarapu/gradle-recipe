package org.openrewrite.samples;

import java.util.Calendar;
import java.util.Collections;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.internal.PropertyPlaceholderHelper;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaSourceFile;
import org.openrewrite.java.tree.TextComment;
import org.openrewrite.marker.Markers;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AddLicenseHeader extends Recipe {
    /**
     * A method pattern that is used to find matching method declarations/invocations.
     * See {@link  MethodMatcher} for details on the expression's syntax.
     */
    @Option(displayName = "License text",
            description = "The license header text without the block comment. May contain ${CURRENT_YEAR} property.",
            example = "Copyright ${CURRENT_YEAR} the original author or authors...")
    String licenseText;

    @Override
    public String getDisplayName() {
        return "Add license header";
    }

    @Override
    public String getDescription() {
        return "Adds license headers to Java source files when missing. Does not override existing license headers.";
    }

    @Override
    protected JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public JavaSourceFile visitJavaSourceFile(JavaSourceFile cu, ExecutionContext executionContext) {
                if (cu.getComments().isEmpty()) {
                    PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}", null);
                    String formattedLicenseText = "\n * " + propertyPlaceholderHelper.replacePlaceholders(licenseText,
                            k -> {
                                if (k.equals("CURRENT_YEAR")) {
                                    return Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
                                }
                                return System.getProperty(k);
                            }).replace("\n", "\n * ") + "\n ";

                    cu = cu.withComments(Collections.singletonList(
                            new TextComment(true, formattedLicenseText, "\n", Markers.EMPTY)
                    ));
                }
                return cu;
            }

            @Override
            public J.Import visitImport(J.Import _import, ExecutionContext executionContext) {
                // short circuit everything else
                return _import;
            }

            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext executionContext) {
                // short circuit everything else
                return classDecl;
            }
        };
    }
}