package com.example.temp;

import cn.hutool.core.util.RandomUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

/**
 * TODO
 *
 * @author suhai
 * @since 2022-03-17 13:45
 */
class SortTest {
    @Data
    public static class People {
        private String name;
        private Integer age;
    }

    /**
     * String类型的集合排序
     */
    @Test
    void sortListTest1() {
        List<String> list = Arrays.asList("王羲之", "张三丰", "vbe", "123", "李斯", "bf", "王淼", "鬼谷子", "415", "秦始皇");
        System.out.println("排序前：" + list);
        list.sort(String::compareTo);
        System.out.println("正序后：" + list);
        Collections.sort(list, Comparator.reverseOrder());
        System.out.println("逆序后：" + list);
        List<Integer> list2 = Arrays.asList(2, 4, null, 1, -3, 34, -123, 0, 54, 2);
        System.out.println("排序前：" + list2);
        list2.sort(Comparator.nullsFirst(Integer::compareTo));
        System.out.println("正序后：" + list2);
        list2.sort(Comparator.reverseOrder());
        System.out.println("逆序后：" + list2);
    }

    @Test
    void sortListTest2() {
        List<String> list = Arrays.asList("王羲之", "张三丰", "vbe", "123", "李斯", "bf", "王淼", "鬼谷子", "415", "秦始皇");
        List<Map<String, Integer>> mapList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Integer> map = new HashMap<>();
            for (int j = 0; j < 5; j++) {
                map.put(list.get(RandomUtil.randomInt(list.size())), RandomUtil.randomInt(0, 50));
            }
            System.out.println(map.hashCode());
            mapList.add(map);
        }
        System.out.println("排序前：" + mapList);
        mapList.sort(Comparator.comparing(m -> m.get("王羲之"), Comparator.nullsLast(Integer::compareTo)));
        System.out.println("正序后：" + mapList);
    }

    @Test
    void mapListTest() {
        List<String> list = Arrays.asList("attr_1", "attr_2", "attr_3", "attr_4");
        List<Map<String, Integer>> mapList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Map<String, Integer> map = new HashMap<>();
            for (int j = 0; j < list.size(); j++) {
                map.put(list.get(j), RandomUtil.randomInt(0, 50));
            }
            mapList.add(map);
        }
        mapList.get(2).put("attr_2", null);
        mapList.add(null);
        System.out.println("排序前：" + mapList);
        mapList.sort(Comparator.nullsFirst(Comparator.comparing(m -> m.get("attr_2"), Comparator.nullsLast(Integer::compareTo))));
        System.out.println("正序后：" + mapList);
        mapList.sort(Comparator.comparing(m -> m.get("attr_2"), Comparator.reverseOrder()));
        System.out.println("逆序后：" + mapList);

    }

    @Test
    void sortListTest3() {
        List<People> peopleList = normalPeople(4);
        System.out.println("排序前：" + peopleList);
        peopleList.sort(Comparator.comparing(People::getName));
        System.out.println("正序后1：" + peopleList);
        peopleList.sort(Comparator.comparing(People::getName).reversed().thenComparing(People::getAge).reversed());
        System.out.println("正序后2：" + peopleList);
        peopleList.sort(Comparator.comparing(People::getName).thenComparing(People::getAge, Comparator.reverseOrder()));
        System.out.println("正序后3：" + peopleList);
    }

    /**
     * 正常对象，没有空属性，也不包含空对象
     *
     * @param repeat 循环因子
     * @return
     */
    List<People> normalPeople(int repeat) {
        List<String> nameList = Arrays.asList("王羲之", "张三丰", "vbe", "123", "李斯", "bf", "王淼", "鬼谷子", "415", "秦始皇");
        List<People> peopleList = new ArrayList<>();
        IntStream.range(0, repeat).forEach(x -> {
            People people = new People();
            people.setName(nameList.get(RandomUtil.randomInt(nameList.size())));
            people.setAge(RandomUtil.randomInt(0, 100));
            peopleList.add(people);
        });
        return peopleList;
    }

    List<People> includeOneNullPeople(int repeat) {
        List<String> nameList = Arrays.asList("王羲之", "张三丰", "vbe", "123", "李斯", "bf", "王淼", "鬼谷子", "415", "秦始皇");
        List<People> peopleList = new ArrayList<>();
        int randomInt = RandomUtil.randomInt(nameList.size());
        IntStream.range(0, repeat).forEach(x -> {
            People people = null;
            if (x == randomInt) {
                peopleList.add(people);
            } else {
                people = new People();
                people.setName(nameList.get(RandomUtil.randomInt(nameList.size())));
                people.setAge(RandomUtil.randomInt(0, 100));
                peopleList.add(people);
            }
        });
        return peopleList;
    }


    List<People> includeMoreNullPeople(int repeat) {
        List<String> nameList = Arrays.asList("王羲之", "张三丰", "vbe", "123", "李斯", "bf", "王淼", "鬼谷子", "415", "秦始皇");
        List<People> peopleList = new ArrayList<>();
        IntStream.range(0, repeat).forEach(x -> {
            if (0 == (x & (x - 1))) {
                peopleList.add(null);
            } else {
                People people = new People();
                people.setName(nameList.get(RandomUtil.randomInt(nameList.size())));
                people.setAge(RandomUtil.randomInt(0, 100));
                peopleList.add(people);
            }
        });
        return peopleList;
    }

    List<People> customerPeople(int repeat) {
        List<String> nameList = Arrays.asList("王羲之", "张三丰", "vbe", "123", "李斯", "bf", "王淼", "鬼谷子", "415", "秦始皇");
        List<People> peopleList = new ArrayList<>();
        IntStream.range(0, repeat).forEach(x -> {
            if (3 == x) {
                peopleList.add(new People());
            } else if (RandomUtil.randomInt(nameList.size()) == x) {
                People ccc = new People();
                ccc.setAge(45);
            } else {
                People people = new People();
                people.setName(nameList.get(RandomUtil.randomInt(nameList.size())));
                people.setAge(RandomUtil.randomInt(0, 100));
                peopleList.add(people);
            }
        });
        return peopleList;
    }
}
