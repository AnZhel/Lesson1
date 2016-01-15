package task2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)

@interface Saver {}

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)

@interface SaveTo {
    public String path();
}

@SaveTo(path = "c:/Курсы/file.txt")
class TextContainer {
    String text;
    public TextContainer(String text){
        this.text = text;
    }

    @Saver
    public void save(String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        try {
            fw.write(this.text);}
        finally {
            fw.close();
        }
    }
}

public class Main {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        TextContainer tc = new TextContainer("Simple text.");
        Class<?> cl = tc.getClass();
        if (cl.isAnnotationPresent(SaveTo.class)){
            Method[] methods = cl.getMethods();
            for (Method method:methods) {
                SaveTo ann = cl.getAnnotation(SaveTo.class);
                String path = ann.path();
                if (method.isAnnotationPresent(Saver.class)){
                    method.invoke(tc, path);
                }
            }
        }
        else System.out.println("Class "+cl.getName()+" doesn't have annotation!");
    }
}
