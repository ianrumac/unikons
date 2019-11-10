package com.lotuslambda.unikons

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Process::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(UnionProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class UnionProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(Union::class.java.name)

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val transformer = TransformUnion(processingEnv.filer)
        val path = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        roundEnv.getElementsAnnotatedWith(Union::class.java).filter {
            it.kind == ElementKind.CLASS
        }.map {methodElement ->
            transformer(methodElement, processingEnv.elementUtils.getPackageOf(methodElement).qualifiedName.toString())
        }.forEach {
            writeToFile(it,path!!)
        }
        return true
    }

    private fun writeToFile(file: FileSpec, path: String){
        val writeTo = File(path,"${file.name}.kt").apply {
            parentFile.mkdir()
        }
        file.writeTo(writeTo)
    }
}