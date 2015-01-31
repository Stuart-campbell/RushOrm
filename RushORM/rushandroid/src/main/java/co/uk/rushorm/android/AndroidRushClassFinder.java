package co.uk.rushorm.android;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushClassFinder;
import co.uk.rushorm.core.RushConfig;
import co.uk.rushorm.core.annotations.RushTableAnnotation;
import dalvik.system.DexFile;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by stuartc on 11/12/14.
 */
public class AndroidRushClassFinder implements RushClassFinder {

    private final Context content;

    public AndroidRushClassFinder(Context content) {
        this.content = content;
    }

    @Override
    public List<Class> findClasses(RushConfig rushConfig) {
        return getDomainClasses(content, rushConfig);
    }

    /* Loading classes code */

    private static List<Class> getDomainClasses(Context context, RushConfig rushConfig) {
        List<Class> domainClasses = new ArrayList<Class>();
        try {
            for (String className : getAllClasses(context)) {
                Class domainClass = getRushClass(className, context, rushConfig);
                if (domainClass != null) domainClasses.add(domainClass);

            }
        } catch (IOException | PackageManager.NameNotFoundException e) {
            Log.e("Rush", e.getMessage());
        }
        return domainClasses;
    }

    private static Class getRushClass(String className, Context context, RushConfig rushConfig) {
        Class<?> discoveredClass = null;
        try {
            discoveredClass = Class.forName(className, true, context.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            Log.e("Rush", e.getMessage());
        }



        if (discoveredClass != null
             && Rush.class.isAssignableFrom(discoveredClass)
             && !Rush.class.equals(discoveredClass)
             && (discoveredClass.isAnnotationPresent(RushTableAnnotation.class) || !rushConfig.requireTableAnnotation())
             && !Modifier.isAbstract(discoveredClass.getModifiers())) {

            return discoveredClass;

        } else {
            return null;
        }
    }


    private static List<String> getAllClasses(Context context) throws PackageManager.NameNotFoundException, IOException {
        String path = getSourcePath(context);
        List<String> classNames = new ArrayList<String>();
        try {
            DexFile dexfile = new DexFile(path);
            Enumeration<String> dexEntries = dexfile.entries();
            while (dexEntries.hasMoreElements()) {
                classNames.add(dexEntries.nextElement());
            }
        } catch (NullPointerException e) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = classLoader.getResources("");
            List<String> fileNames = new ArrayList<String>();
            while (urls.hasMoreElements()) {
                String classDirectoryName = urls.nextElement().getFile();
                if (classDirectoryName.contains("bin") || classDirectoryName.contains("classes")) {
                    File classDirectory = new File(classDirectoryName);
                    for (File filePath : classDirectory.listFiles()) {
                        populateFiles(filePath, fileNames, "");
                    }
                    classNames.addAll(fileNames);
                }
            }
        }
        return classNames;
    }

    private static void populateFiles(File path, List<String> fileNames, String parent) {
        if (path.isDirectory()) {
            for (File newPath : path.listFiles()) {
                if ("".equals(parent)) {
                    populateFiles(newPath, fileNames, path.getName());
                } else {
                    populateFiles(newPath, fileNames, parent + "." + path.getName());
                }
            }
        } else {
            String pathName = path.getName();
            String classSuffix = ".class";
            pathName = pathName.endsWith(classSuffix) ?
                    pathName.substring(0, pathName.length() - classSuffix.length()) : pathName;
            if ("".equals(parent)) {
                fileNames.add(pathName);
            } else {
                fileNames.add(parent + "." + pathName);
            }
        }
    }

    private static String getSourcePath(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
    }

}
