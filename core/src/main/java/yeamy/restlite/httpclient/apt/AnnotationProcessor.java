package yeamy.restlite.httpclient.apt;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Annotation Processor Looking up HttpClient to Generate Java Class.
 */
public class AnnotationProcessor extends AbstractProcessor {

    private final Set<String> supportedAnnotationTypes = new HashSet<>();
    private boolean hasInjectProvider;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        supportedAnnotationTypes.add(HttpClient.class.getCanonicalName());
        hasInjectProvider = processingEnv.getElementUtils()
                .getTypeElement("yeamy.restlite.annotation.InjectProvider") != null;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotationTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(HttpClient.class)) {
            if (element.getKind() == ElementKind.ANNOTATION_TYPE) {
                HttpClient template = element.getAnnotation(HttpClient.class);
                Element type = processingEnv.getTypeUtils().asElement(element.asType());
                for (Element element1 : roundEnv.getElementsAnnotatedWith((TypeElement) type)) {
                    try {
                        new SourceMethod(processingEnv, hasInjectProvider, (TypeElement) element1, template).create();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (element.getKind() == ElementKind.INTERFACE) {
                try {
                    new SourceMethod(processingEnv, hasInjectProvider, (TypeElement) element).create();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}