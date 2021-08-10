package com.example.myapplication;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class FixMamager {

    private static HashSet<File> loadedDex = new HashSet<>();

    static {
        loadedDex.clear();
    }

    public static void loadFixDex(@NonNull Context context) throws Exception {
        File fixedDir = context.getDir("fixed_dir", Context.MODE_PRIVATE);
        File[] files = fixedDir.listFiles();
        for (File file : files) {
            if (file.getName().startsWith("fixed") && file.getName().endsWith(".dex")) {
                loadedDex.add(file);
            }
        }

        String optDir = fixedDir.getAbsoluteFile() + File.separator + "opt_dex";
        File fpot = new File(optDir);
        if (!fpot.exists()) {
            fpot.mkdirs();
        }

        for (File dex : loadedDex) {
            //第一步：获取当前引用的dexElements
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            Class<?> baseDexClassLoaderClazz = pathClassLoader.getClass().getSuperclass();

            Field pathListField = baseDexClassLoaderClazz.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathListValue = pathListField.get(pathClassLoader);

            Field dexElementsField = pathListValue.getClass().getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            Object dexElementsValue = dexElementsField.get(pathListValue);

            //第二步：获取已修复到dexElements
            DexClassLoader dexClassLoader = new DexClassLoader(dex.getAbsolutePath(), optDir, null, context.getClassLoader());
            Object dexPathListValue = pathListField.get(dexClassLoader);
            Object fixedDexElementsValue = dexElementsField.get(dexPathListValue);

            //第三步：合并，修复过到dex放在前面优先加载
            int length = Array.getLength(dexElementsValue);
            int dexLength = Array.getLength(fixedDexElementsValue);
            int newLength = length + dexLength;

            Class<?> componentType = fixedDexElementsValue.getClass().getComponentType();

            Object newArray = Array.newInstance(componentType, newLength);

            for (int i = 0; i < newLength; i++) {
                if (i < dexLength) {
                    Array.set(newArray, i, Array.get(fixedDexElementsValue, i));
                } else {
                    Array.set(newArray, i, Array.get(dexElementsValue, i - dexLength));
                }
            }

            dexElementsField.set(pathListValue, newArray);
        }

    }

}


















