[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

 

I love Kotlin, but it lacks unions. 
 
 This annotation processor takes a class annotated with @Union and returns a sealed class
 wrapper around it pretending to be an union


 Example:

```kotlin 
@Union([String::class,Bar::class,Double::class)
class Foo
```
Creates a:
`sealed class FooUnion<T>(val value: T>`

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
  

Usage:
```groovy
    implementation 'com.github.lotuslambda:master-SNAPSHOT'
    kapt 'com.github.lotuslambda:unikons:master-SNAPSHOT'

```
