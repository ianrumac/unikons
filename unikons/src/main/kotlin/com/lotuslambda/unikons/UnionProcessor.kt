package com.lotuslambda.unikons

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind.ERROR

private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class UnionProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(Union::class.java.name)

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val annotatedElements = roundEnv.getElementsAnnotatedWith(Union::class.java)
        if (annotatedElements.isEmpty()) return false

        val path = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        val transformer = TransformUnion()
        annotatedElements
            .map { methodElement ->
                transformer(
                    element = methodElement,
                    packageOf = processingEnv.elementUtils.getPackageOf(methodElement).qualifiedName.toString()
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
