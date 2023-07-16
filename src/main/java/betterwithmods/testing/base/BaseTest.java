package betterwithmods.testing.base;

import betterwithmods.BWMod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseTest {

    private void before() {
        Class<? extends BaseTest> clazz = this.getClass();
        Method[] methods = clazz.getMethods();
        for(Method method: methods) {
            if(method.isAnnotationPresent(Before.class)) {
                try {
                    method.invoke(this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void run() {
        int passed = 0, failed = 0, count = 0;
        Class<? extends BaseTest> clazz = this.getClass();

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                before();
                try {
                    method.invoke(this);
                    BWMod.logger.warn("{} - Test '{}' - passed", ++count, method.getName());
                    passed++;
                } catch (Throwable ex) {
                    BWMod.logger.error("{} - Test '{}' - failed: {}", ++count, method.getName(), ex.getCause());
                    failed++;
                }

            }
        }
        BWMod.logger.warn("Result {} : Total : {}, Passed: {}, Failed {}", clazz.getSimpleName(), count, passed, failed);

    }

}
