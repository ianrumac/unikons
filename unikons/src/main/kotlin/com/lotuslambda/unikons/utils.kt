package com.lotuslambda.unikons

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import me.eugeniomarletti.kotlin.metadata.shadow.name.FqName
import me.eugeniomarletti.kotlin.metadata.shadow.platform.JavaToKotlinClassMap
import javax.lang.model.type.TypeMirror

fun TypeMirror.toKotlinClassName(): ClassName {
    var currentClass = ClassName.bestGuess(asTypeName().toString())
    val typeName = asTypeName().toString()

    //Simple check to see if we're using a Java type instead of a Kotlin one
    if (currentClass.packageName.isFromJava() && typeName.isFromJava()) {
        val kotlinVersion = JavaToKotlinClassMap
            .mapJavaToKotlin(FqName(currentClass.canonicalName))

        if (kotlinVersion != null) {
            currentClass = ClassName(
                kotlinVersion.packageFqName.asString(),
                kotlinVersion.asSingleFqName().shortName().asString()
            )
        }
    }

    return currentClass
}

private fun String.isFromJava(): Boolean {
    return startsWith("java.lang")
}