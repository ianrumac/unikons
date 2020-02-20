[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![](https://jitpack.io/v/lotuslambda/unikons.svg)](https://jitpack.io/#lotuslambda/unikons/0.3)

 

I love Kotlin, but it lacks unions. 
 
 This annotation processor takes a class annotated with @Union and returns a sealed class
 wrapper around it pretending to be an union


 Example:

```kotlin 
@Union(String::class,Bar::class,Double::class)
class Foo
```
Creates a:
`sealed class FooUnion`

Creating a unionized type is as simple as 
```kotlin
FooUnion.String(value)
FooUnion.Bar(value)
FooUnion.Double(value)
```

Getting a value from the union is as simple as calling `.value`
```kotlin
  val x : FooUnion = retrunsAFooUnion() 
  when(x.value){
    is String -> do()
    is Bar -> a()
    is Double -> thing()
  }
  ```
  
  Type erasure happens so sorry about that. There's inline classes, maybe try with those.
  That's all.
  

Installing:

```groovy
    implementation 'com.github.lotuslambda:0.3.3'
    annotationProcessor 'com.github.lotuslambda:unikons:0.3.3'
    kapt 'com.github.lotuslambda:unikons:0.3.3'

```
