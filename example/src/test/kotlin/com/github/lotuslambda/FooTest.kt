package com.github.lotuslambda

import org.junit.Test

class FooTest {

    @Test
    fun foo() {
        FooUnion.Bar(Bar())
        FooUnion.Double(1.2)
        FooUnion.String("Test")
    }
}
