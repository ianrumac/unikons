package com.lotuslambda.unikons

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Union(val types: Array<KClass<*>>)