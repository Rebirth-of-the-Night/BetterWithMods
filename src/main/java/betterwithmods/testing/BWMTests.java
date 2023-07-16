package betterwithmods.testing;

import betterwithmods.testing.base.BaseTest;
import com.google.common.collect.Lists;

import java.util.List;

public class BWMTests {

    private static final List<Class<? extends BaseTest>> TESTS = Lists.newArrayList();

    static {
        TESTS.add(CookingPotTests.class);
    }

    private static void runTest(Class<? extends BaseTest> clazz) {
        try {
            clazz.newInstance().run();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void runTests() {
        for (Class<? extends BaseTest> clazz : TESTS) {
            runTest(clazz);
        }
    }

}
