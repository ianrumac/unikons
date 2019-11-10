import com.lotuslambda.unikons.UnionProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile

data class Bar(val baz: String)

@Union([String::class, Bar::class, Double::class])
class Foo


@org.junit.Test
fun main() {
    val kotlinSource = SourceFile.kotlin(
        "KClass.kt", """
        data class Bar(val baz: String)

@Union([String::class,Bar::class,Double::class])
class Foo

    """
    )

    val result = KotlinCompilation().apply {
        sources = listOf(kotlinSource)


        // pass your own instance of an annotation processor
        annotationProcessors = listOf(UnionProcessor())
        inheritClassPath = true
        messageOutputStream = System.out// see diagnostics in real time
    }.compile()
}

sealed class FooUnion<UnionedType>(
    val value: UnionedType
) {
    private class StringFooUnion(
        value: String
    ) : FooUnion<String>(value)

    private class BarFooUnion(
        value: Bar
    ) : FooUnion<Bar>(value)

    private class DoubleFooUnion(
        value: Double
    ) : FooUnion<Double>(value)

    companion object {
        fun String(value: String) = FooUnion.StringFooUnion(value) as FooUnion<String>

        fun Bar(value: Bar) = FooUnion.BarFooUnion(value) as FooUnion<Bar>

        fun Double(value: Double) = FooUnion.DoubleFooUnion(value) as FooUnion<Double>
    }
}
