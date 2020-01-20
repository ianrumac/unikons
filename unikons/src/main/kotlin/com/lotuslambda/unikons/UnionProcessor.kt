package com.lotuslambda.unikons

import com.squareup.kotlinpoet.FileSpec
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind.ERROR

private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class UnionProcessor : KotlinAbstractProcessor() {
    val transformer = TransformUnion()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(Union::class.java.name)

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val annotatedElements = roundEnv.getElementsAnnotatedWith(Union::class.java)
        if (annotatedElements.isEmpty()) return false
        val environment = (this as KotlinAbstractProcessor).processingEnv
        val path = environment.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            environment.messager.printMessage(ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        annotatedElements
            .map { methodElement ->
                transformer(
                    element = methodElement,
                    packageOf = environment.elementUtils.getPackageOf(methodElement).qualifiedName.toString()
                )
            }
            .forEach { fileSpec -> writeToFile(fileSpec, path) }

        return true
    }

    private fun writeToFile(file: FileSpec, path: String) {
        val writeTo = File(path, "${file.name}.kt").apply {
            parentFile.mkdirs()
        }
        file.writeTo(writeTo)
    }

}
