package task1;

import java.lang.reflect.*;;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@interface Test {
    public int a();
    public int b();
}

class NewClass {
    public NewClass() {
    }
    @Test(a=2, b=5)
    public void test(int a, int b){
        System.out.println("param a: "+a+" param b: "+b);
    }
}

public class Main {

    public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        NewClass newclass = new NewClass();
        Class<?> cl = newclass.getClass();
        Method[] methods = cl.getMethods();
        for (Method me : methods){
            Test test = me.getAnnotation(Test.class);
            if(me.isAnnotationPresent(Test.class)){
                me.invoke(newclass, test.a(),test.b());
            }
        }
    }

}

