package com.borber.bullplugin;

import com.borber.bullplugin.annotations.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Project: BullPlugin
 * -----------------------------------------------------------
 * Copyright © 2020 | Enaium | All rights reserved.
 */
public class BullPlugin {

    private File pluginPath;

    public BullPlugin(File pluginPath) {
        this.pluginPath = pluginPath;
    }

    public ArrayList<Class<?>> getPlugins() throws Exception {

        if (!pluginPath.exists()) throw new IOException("Path does not exist");

        if (!pluginPath.isDirectory()) throw new IOException("The path is not a directory");

        ArrayList<File> files = new ArrayList<>();
        ArrayList<Class<?>> plugins = new ArrayList<>();

        for (File file : Objects.requireNonNull(pluginPath.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                files.add(file);
            }
        }

        for (File f : files) {
            JarFile jar = new JarFile(f);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }
                String className = entry.getName().substring(0, entry.getName().length() - 6);
                className = className.replace('/', '.');
                Class<?> clazz = new URLClassLoader(new URL[]{f.toURI().toURL()}, Thread.currentThread().getContextClassLoader()).loadClass(className);
                if (clazz.getAnnotation(Plugin.class) != null) {
                    plugins.add(clazz);
                }
            }
        }
        return plugins;
    }

    public ArrayList<Class<?>> getPluginsFromJar() throws Exception{
            JarFile jar = new JarFile(pluginPath);
            ArrayList<Class<?>> plugins = new ArrayList<>();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }
                String className = entry.getName().substring(0, entry.getName().length() - 6);
                className = className.replace('/', '.');
                Class<?> clazz = new URLClassLoader(new URL[]{pluginPath.toURI().toURL()}, Thread.currentThread().getContextClassLoader()).loadClass(className);
                if (clazz.getAnnotation(Plugin.class) != null) {
                    plugins.add(clazz);
                }
            }
        return plugins;
    }


}
