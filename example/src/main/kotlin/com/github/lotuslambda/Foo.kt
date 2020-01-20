package com.github.lotuslambda

import com.lotuslambda.unikons.Union

class Bar
@Union(Bar::class, Double::class, String::class)
class Foo
