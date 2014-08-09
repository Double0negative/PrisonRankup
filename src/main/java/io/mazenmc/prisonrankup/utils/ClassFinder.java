/**
 PrisonRankup, the most feature-packed rankup plugin out there.
 Copyright (C) 2014 Mazen K.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.mazenmc.prisonrankup.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassFinder {

    public static <T> List<Class<? extends T>> find(String pkg, Class<T> filter, File f) throws Exception {
        List<Class<? extends T>> classes = new ArrayList<>();
        pkg = pkg.replaceAll("\\.", File.separator);

        JarFile jar = new JarFile(f);
        Enumeration<JarEntry> enumeration = jar.entries();
        JarEntry entry = null;

        while(enumeration.hasMoreElements() && (entry = enumeration.nextElement()) != null) {
            String name = entry.getName();
            Class<?> cls;

            if(name.endsWith(".class") && name.startsWith(pkg) && !(name.contains("$")) &&
                    (filter.isAssignableFrom(Class.forName(name.replaceAll(".class", "").replaceAll(File.separator, "."))) || filter.equals(Object.class))) {
                cls = Class.forName(name.replaceAll(".class", "").replaceAll(File.separator, "."));
                classes.add(cls.asSubclass(filter));
            }
        }

        return classes;
    }

    public static <T> List<Class<? extends T>> find(String pkg, Class<T> filter, Plugin f) throws Exception {
        return find(pkg, filter, new File(f.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ")));
    }

    public static List<Class<?>> find(String pkg, File f) throws Exception {
        return find(pkg, Object.class, f);
    }

    public static <T> List<Class<? extends T>> find(String pkg, Class<T> filter) throws Exception{
        List<Class<? extends T>> classes = new ArrayList<>();

        for(File f : Bukkit.getWorldContainer().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".jar");
            }
        })) {
            classes.addAll(find(pkg, filter, f));
        }

        return classes;
    }

    public static List<Class<?>> find(String pkg) throws Exception {
        return find(pkg, Object.class);
    }
}