package org.openrewrite.samples

import org.openrewrite.java.JavaRecipeTest
import org.junit.jupiter.api.Test

class FindReplaceTest: JavaRecipeTest {
	@Test
	fun replaceText() = assertChanged(
		recipe = FindReplaceText("Hi","Hello"),
		before = """
			package com.naga;
			class A {
				String hello = "Hi";
			}
		""",
		after = """
			package com.naga;
			class A {
				String hello = "Hello";
			}
		"""
	)
}