package com.lotuslambda.unikons

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Union(vararg val types: KClass<*>)