import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(UnionProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class UnionProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(Union::class.simpleName!!)

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val transformer = TransformUnion(processingEnv.filer)

        roundEnv.getElementsAnnotatedWith(Union::class.java).forEach { methodElement ->
            if (methodElement.kind != ElementKind.CLASS) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Can only be applied to classes, element: $methodElement "
                )
                return false
            }
            transformer(methodElement, processingEnv.elementUtils.getPackageOf(methodElement).qualifiedName.toString())

        }
        return true;
    }
}