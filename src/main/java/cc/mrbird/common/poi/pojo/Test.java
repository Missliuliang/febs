package cc.mrbird.common.poi.pojo;

import java.util.*;

public class Test {

    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>();
        Map<Object,String> stringMap=new HashMap<>();
        stringList.add("zzz1");
        stringList.add("aaa2");
        stringList.add("bbb2");
        stringList.add("fff1");
        stringList.add("fff2");
        stringList.add("aaa1");
        stringList.add("bbb1");
        stringList.add("zzz2");
       // stringList.stream().filter(dt-> dt.startsWith("a")).forEach(System.out::println);
       // stringList.stream().forEach(string->{System.out.println(string.endsWith("1")); });
        // stringList.stream().sorted( (a ,b) -> a.compareTo(b)).sorted((f,z)->z.compareTo(f)).forEach(System.out::println);
        //boolean a1 = stringList.stream().anyMatch(a -> a.startsWith("a")); true
       // boolean a2 = stringList.stream().allMatch(a -> a.startsWith("a")); false
        //Optional<String> reduce = stringList.stream().sorted().reduce((a, b) -> a + "**" + b);
       // reduce.ifPresent(System.out::println);
        //System.out.println(reduce.get());
      /*  stringList.stream().map(String::toUpperCase).forEach(s -> {
            System.out.println(s);
        });*/
        stringMap.put(10,null);
      /*  stringMap.putIfAbsent(10,"0000");
        stringMap.putIfAbsent(10,"0001");
       stringMap.forEach((k,v)->{
           System.out.println(k+"---"+v);
       });*/
    /*  stringMap.put(11,"value22");//替换value值
       *//* String compute = stringMap.compute(11, (key, val) -> key + val);
        System.out.println(compute);  11value22*//*
       stringMap.computeIfPresent(11,(key,val)->key+val);
        System.out.println(stringMap.get(11));

        new Thread(()-> System.out.println("----")).start();*/

        String[] players = {"Rafael Nadal", "Novak Djokovic",
                "Stanislas Wawrinka", "David Ferrer",
                "Roger Federer", "Andy Murray",
                "Tomas Berdych", "Juan Martin Del Potro",
                "Richard Gasquet", "John Isner"};
       Arrays.asList(players).sort((String s1, String s2) -> (s1.compareTo(s2)));
       String name ="123";
       int age=123;
       String age1=Integer.toString(age);
       int name1=Integer.parseInt(name);


    }
}
