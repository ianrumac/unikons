package com.lotuslambda.unikons

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Filer
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
    operator fun invoke(element: Element, packageOf: String) : FileSpec{
        val union = element.getAnnotation(Union::class.java)
        val unionName = "${element.simpleName}Union"
        val unionClassName = ClassName(packageOf, unionName)
        val generic = TypeVariableName("UnionedType")
        return FileSpec.builder("", unionName)
            .addType(
                TypeSpec.classBuilder(unionClassName)
                    .addTypeVariable(generic)
                    .primaryConstructor(
                        FunSpec.constructorBuilder().addParameter(VALUE, generic).build()
                    ).addModifiers(KModifier.SEALED)
                    .addProperty(PropertySpec.builder(VALUE, generic).initializer(
                        VALUE
                    ).build())
                    .apply {
                        //way to access elements of annotation if they're a Class/K<Class>
                        val typesInUnion: List<TypeMirror> = try {
                            union.types.map { it as TypeMirror }
                        } catch (e: MirroredTypesException) {
                            e.typeMirrors
                        }

                        //holds creation methods
                        val companionObject = TypeSpec.companionObjectBuilder()
                        //create subclasses for each type
                        typesInUnion.map {
                            val currentClass = ClassName.bestGuess(it.asTypeName().toString())
                            val name = currentClass.simpleName
                            val classForTypeName = "$name$unionName"
                            val componentMethod = companionMethodBuilderFor(
                                it.asTypeName(), name, classForTypeName, unionName
                            ).build()
                            val generatedSubclass = subclassBuilderFor(
                                classForTypeName, it, unionClassName, currentClass
                            ).build()
                            Pair(generatedSubclass, componentMethod)
                        }.forEach {
                            addType(it.first)
                            companionObject.addFunction(it.second)
                        }

                        addType(companionObject.build())
                    }.build()

            ).build()
    }

    private fun subclassBuilderFor(
        classForTypeName: String,
        it: TypeMirror,
        unionClassName: ClassName,
        currentClass: ClassName
    ): TypeSpec.Builder {
        return TypeSpec.classBuilder(classForTypeName)
            .addModifiers(KModifier.PRIVATE)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(VALUE, it.asTypeName()).build()
            )
            .superclass(unionClassName.parameterizedBy(currentClass))
            .addSuperclassConstructorParameter(VALUE, it)
    }


    private fun companionMethodBuilderFor(
        type: TypeName,
        typeName: String,
        className: String,
        unionName: String
    ): FunSpec.Builder {
        return FunSpec.builder(typeName).addParameter(ParameterSpec.builder(VALUE, type).build())
            .addStatement(CodeBlock.of("return $unionName.$className($VALUE) as $unionName<$typeName>").toString())
    }

}
