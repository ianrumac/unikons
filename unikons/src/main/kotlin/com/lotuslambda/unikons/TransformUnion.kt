package com.lotuslambda.unikons

import com.squareup.kotlinpoet.*
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror


/*
* Takes a class annotated with @Union and returns a sealed class
* wrapper around it pretending to be an union
* Example:
*
* @Union([String::class,Bar::class,Double::class])
* class Foo
*
* creates a
*
* sealed class FooUnion<T>(val value: T>
* Creating a unionized type is as simple as FooUnion.String(value)
* Getting a value from the union is as simple as calling unionInstance.value
* */

class TransformUnion {
    companion object {
        private const val VALUE = "value"
    }

    //current annotation -> element and package
    operator fun invoke(element: Element, packageOf: String): FileSpec {
        val union = element.getAnnotation(Union::class.java)
        val unionName = "${element.simpleName}Union"
        val unionClassName = ClassName(packageOf, unionName)

        //way to access elements of annotation if they're a Class/K<Class>

        val typesInUnion: List<TypeMirror> = try {
            union.types.map { it as TypeMirror }
        } catch (e: MirroredTypesException) {
            e.typeMirrors
        }

        return FileSpec.builder(packageOf, unionName)
            .addType(
                TypeSpec
                    .classBuilder(unionClassName)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .build()
                    )
                    .addModifiers(KModifier.SEALED)
                    .apply {
                        //holds creation methods
                        val companionObject = TypeSpec.companionObjectBuilder()
                        //create subclasses for each type
                        typesInUnion
                            .map { it.toKotlinClassName() }
                            .map { typeInUnion ->
                                val name = typeInUnion.simpleName
                                val nameAsUnionClass = "$name$unionName"

                                val companionMethod = companionMethodBuilderFor(
                                    type = typeInUnion,
                                    typeName = name,
                                    unionName = unionName
                                ).build()

                                val generatedSubclass = subclassBuilderFor(
                                    classForTypeName = name,
                                    unionClassName = unionClassName,
                                    currentClass = typeInUnion
                                ).build()

                                Pair(generatedSubclass, companionMethod)
                            }.forEach { (typeSpec, companionMethod) ->
                                addType(typeSpec)
                                companionObject.addFunction(companionMethod)
                            }

                        addType(companionObject.build())

                    }.build()
            ).build()
    }

    private fun subclassBuilderFor(
        classForTypeName: String,
        unionClassName: ClassName,
        currentClass: ClassName
    ): TypeSpec.Builder = TypeSpec.classBuilder(classForTypeName)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter(VALUE, currentClass)
                .build()
        )
        .addProperty(
            PropertySpec.builder(VALUE, currentClass)
                .initializer(VALUE)
                .build()
        )
        .superclass(unionClassName)

    private fun companionMethodBuilderFor(
        type: ClassName,
        typeName: String,
        unionName: String
    ): FunSpec.Builder = FunSpec.builder(typeName)
        .addParameter(ParameterSpec.builder(VALUE, type).build())
        .addStatement(CodeBlock.of("return $unionName.$typeName($VALUE)").toString())
}
