package com.example.temp;

import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.range;

/**
 * TODO
 *
 * @author suhai
 * @since 2021-06-02 10:13
 */
public class StreamTest {

    @Test
    void repeatTest() {
        range(0, 10).forEach(System.out::println);
        repeat(10, StreamTest::hi);
    }

    private void repeat(int n, Runnable action) {
        range(0, n).forEach(i -> action.run());
    }

    private static void hi() {
        System.out.println("hi");
    }
}
