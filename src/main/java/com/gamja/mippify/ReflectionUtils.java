package com.gamja.mippify;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {
    public static Field tryGetField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            Mippify.LOGGER.error("Failed to get field in {}", clazz.getCanonicalName(), e);
            return null;
        }
    }

    public static Method tryGetMethod(Class<?> clazz, String name, Class<?>... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, args);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            Mippify.LOGGER.error("Failed to get method in {}", clazz.getCanonicalName(), e);
            return null;
        }
    }

    public static Object tryGet(Field field, Object target) {
        try {
            return field.get(target);
        } catch (Exception e) {
            String name = field == null ? "(unknown)" : field.getName();
            Mippify.LOGGER.error("Failed to get field {}", name, e);
            return null;
        }
    }

    public static void trySet(Field field, Object object, Object value) {
        try {
            field.set(object, value);
        } catch (Exception e) {
            String name = field == null ? "(unknown)" : field.getName();
            Mippify.LOGGER.error("Failed to get field {}", name, e);
        }
    }

    public static Object tryInvoke(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            String name = method == null ? "(unknown)" : method.getName();
            Mippify.LOGGER.error("Failed to invoke method {}", name, e);
            return null;
        }
    }
}
