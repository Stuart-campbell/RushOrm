package co.uk.rushorm.android;

import co.uk.rushorm.core.Logger;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushClassFinder;
import co.uk.rushorm.core.RushConfig;
import co.uk.rushorm.core.annotations.RushTableAnnotation;
import dalvik.system.DexFile;

import android.content.Context;
import android.content.pm.PackageManager;

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
    private final Logger logger;

    public AndroidRushClassFinder(Context content, Logger logger) {
        this.content = content;
        this.logger = logger;
    }

    @Override
    public List<Class<? extends Rush>> findClasses(RushConfig rushConfig) {
        return getRushClasses(content, rushConfig);
    }

    /* Loading classes code */

    private List<Class<? extends Rush>> getRushClasses(Context context, RushConfig rushConfig) {
        List<Class<? extends Rush>> domainClasses = new ArrayList<>();
        try {
            for (String className : getAllClasses(context)) {
                if(classNameContainedInPackageRoots(className, rushConfig.getPackages())) {
                    Class<? extends Rush> domainClass = getRushClass(className, context, rushConfig);
                    if (domainClass != null) {
                        domainClasses.add(domainClass);
                    }
                }
            }
        } catch (IOException | PackageManager.NameNotFoundException e) {
            logger.logError(e.getMessage());
        }
        return domainClasses;
    }

    private boolean classNameContainedInPackageRoots(String className, List<String> packageRoots) {
        if(packageRoots == null) {
            return true;
        }
        for(String packageRoot : packageRoots) {
            if(className.contains(packageRoot)) {
                return true;
            }
        }
        return false;
    }

    private Class<? extends Rush> getRushClass(String className, Context context, RushConfig rushConfig) {
        Class<?> discoveredClass = null;
        try {
            discoveredClass = Class.forName(className, true, context.getClass().getClassLoader());
        } catch (Throwable e) {
            logger.logError(e.getMessage());
        }

        if (discoveredClass != null
             && Rush.class.isAssignableFrom(discoveredClass)
             && !Rush.class.equals(discoveredClass)
             && (discoveredClass.isAnnotationPresent(RushTableAnnotation.class) || !rushConfig.requireTableAnnotation())
             && !Modifier.isAbstract(discoveredClass.getModifiers())) {

            return (Class<? extends Rush>) discoveredClass;

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
