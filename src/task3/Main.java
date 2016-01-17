package task3;

import javax.print.DocFlavor;
import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public String toString() {
        return "Number = "+this.number+". Description = "+this.description+". Parent = "+this.parent;
    }
}

class Serializer{
    public static void serialize(Object object, BufferedWriter bw, int level) throws IOException, IllegalAccessException {
        if (bw==null) bw = new BufferedWriter(new FileWriter("c:\\Java\\serialize.txt"));
        Class<?> cl = object.getClass();
        Field[] fields = cl.getDeclaredFields();

        for (Field field:fields ) {
            String def = "";
            if(field.isAnnotationPresent(Save.class)){
                for (int i = 0; i < level; i++) {
                    def = def + "\t";
                }
                def = def + "Field: {<name:"+field.getName()+"><value:";
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
                    def = def +"object>}\n";
                    bw.write(def);
                    Serializer.serialize(temp,bw,level+1);
                    }
                } else def = def+"unseriasable>\n";
             }
        }
        if (level==0) bw.close();
    }
    public static MyClass deserialize(BufferedReader br) throws IOException, NoSuchFieldException, IllegalAccessException {
        if(br==null) br = new BufferedReader(new FileReader("c:\\Java\\serialize.txt"));
        MyClass myClass = new MyClass();
        String line = "";
        Pattern pattern = Pattern.compile("(.*Field: \\{<name:(.*)><value:(.*)>\\})");
        Class<?> cl = MyClass.class;
        while ((line = br.readLine())!=null){
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()){
                continue;
            }
            String name = matcher.group(2);
            String value = matcher.group(3);
            Field field = cl.getDeclaredField(name);
            if (!field.isAnnotationPresent(Save.class)) continue;
            if (Modifier.isPrivate(field.getModifiers())|| Modifier.isProtected(field.getModifiers())) field.setAccessible(true);
            Class<?> type = field.getType();
            if(type == int.class){
                field.set(myClass,Integer.parseInt(value));
            }
            else if(type == String.class){
                field.set(myClass,value);
            }
            else if(value.equals("object")){
                MyClass vl = Serializer.deserialize(br);
                field.set(myClass,vl);
            }
        }
        return myClass;
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
        MyClass myClass = Serializer.deserialize(null);
        System.out.println(myClass);
    }

}
