package task3;

import javax.print.DocFlavor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
@interface Save{}

class MyClass{
    @Save
    int number;
    @Save
    String description;
    @Save
    MyClass parent;

    public void MyClass(){}

    public void setNumber(int number) {
        this.number = number;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParent(MyClass parent) {
        this.parent = parent;
    }

    public int getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public MyClass getParent() {
        return parent;
    }
}

class Serializer{
    public static void serialize(Object object, BufferedWriter bw, int level) throws IOException, IllegalAccessException {
        if (bw==null) bw = new BufferedWriter(new FileWriter("c:/Курсы/serialize.txt"));
        Class<?> cl = object.getClass();
        Field[] fields = cl.getDeclaredFields();

        for (Field field:fields ) {
            String def = "";
            if(field.isAnnotationPresent(Save.class)){
                for (int i = 0; i < level; i++) {
                    def = def + "\t";
                }
                def = def + "Field: {<name:"+field.getName()+"><type:"+field.getType().getName()+"><value:";
                if (field.getType() == int.class){
                    def = def + field.getInt(object) +">}\n";
                    bw.write(def);
                } else if (field.getType() == String.class){
                    def = def + (String)field.get(object) +">}\n";
                    bw.write(def);
                } else if (field.getType() == MyClass.class){
                    MyClass temp = (MyClass)field.get(object);
                    if(temp==null) {def = def +"null>\n"; bw.write(def);}
                    else {
                    def = def +"object>\n";
                    bw.write(def);
                    Serializer.serialize(temp,bw,level+1);
                    bw.write("}\n");}
                } else def = def+"unseriasable>\n";
             }
        }
        if (level==0) bw.close();

    }

    
}


public class Main {
    public static void main(String[] args) throws Exception {
        MyClass khronus = new MyClass();
        khronus.setNumber(0);
        khronus.setDescription("Khronus");
        MyClass zeus = new MyClass();
        zeus.setNumber(1);
        zeus.setDescription("Zeus");
        zeus.setParent(khronus);
        Serializer.serialize(zeus,null,0);
    }

}
