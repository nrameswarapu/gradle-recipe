/**
 * 
 */
package org.openrewrite.samples;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Vedasri
 *
 * Find and replace a text recipe
 */
public class FindReplaceText extends Recipe {
	
	
	@Option(displayName = "find text",
			description = "text to serach for",
			example = "any string value")
	@NonNull
	private final String findText;
	
	@Option(displayName = "replace text",
			description = "text to replace a search text",
			example = "any string value")
	@NonNull
	private final String replaceText;

	public FindReplaceText(@NonNull @JsonProperty("findText") String findText,
				@NonNull @JsonProperty("replaceText") String replaceText) {
		this.findText = findText;
		this.replaceText = replaceText;
	}
	@Override
	public String getDisplayName() {
		return "Find and replace a text";
	}
	
	@Override
	public String getDescription() {
		return "Find a text and replace with given value";
	}
	
	@Override
	protected JavaIsoVisitor<ExecutionContext> getVisitor(){
		return new JavaIsoVisitor<ExecutionContext>() {
			@Override
			public J.Literal visitLiteral(J.Literal literal, ExecutionContext context){
				if(JavaType.Primitive.String != literal.getType()) {
					return literal;
				}
				
				String value = (String) literal.getValue();
				
				if(!findText.equals(value)) {
					return literal;
				}
				
				return literal.withValue(replaceText).withValueSource("\""+ replaceText +"\"");
			}
		};
	}
}
