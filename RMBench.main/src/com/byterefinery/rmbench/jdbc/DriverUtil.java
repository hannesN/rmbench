/*
 * created 29.04.2005
 * 
 * $Id:DriverUtil.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.jdbc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.byterefinery.rmbench.exceptions.SystemException;


/**
 * with many thanks to {@linkplain http://quantum.sourceforge.net QuantumDB}
 * @author BC Holmes
 */
public class DriverUtil {

    private static Hashtable<String, URLClassLoader> classLoaderCache = 
    	new Hashtable<String, URLClassLoader>();
	
	public static Driver loadDriver(String[] driverFiles, String className) 
        throws SystemException {
		
		Class<Driver> driverClass = loadDriverClass(driverFiles, className);
		try {
            return (Driver) driverClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new SystemException(e);
        }
        catch (IllegalAccessException e) {
            throw new SystemException(e);
        }
	}
	
	@SuppressWarnings("unchecked")
	public static Class<Driver> loadDriverClass(String[] driverFiles, String className) 
		throws SystemException {
		
		if (driverFiles != null) {
			File[] files = toFiles(driverFiles);
			URLClassLoader loader = getURLClassLoader(files);
            try {
                return (Class<Driver>) loader.loadClass(className);
            }
            catch (ClassNotFoundException e) {
                throw new SystemException(e);
            }
		} 
		else {
			try {
                return (Class<Driver>) DriverUtil.class.getClassLoader().loadClass(className);
            }
            catch (ClassNotFoundException e) {
                throw new SystemException(e);
            }
		}
	}
	
    public static String[] getAllDriverNames(String[] driverFiles)
    throws SystemException {
        
        List<Class<Driver>> list = getAllDriversAsList(driverFiles);
        String[] names = new String[list.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = list.get(i).getName();
        }
        return names;
    }
    
    @SuppressWarnings("unchecked")
	public static Class<Driver>[] getAllDrivers(String[] driverFiles) 
        throws SystemException {
        
        List<Class<Driver>> list = getAllDriversAsList(driverFiles);
        return list.toArray(new Class[list.size()]);
    }
    
	public static List<Class<Driver>> getAllDriversAsList(String[] driverFiles) 
        throws SystemException {
        
		List<Class<Driver>> list = new ArrayList<Class<Driver>>();
		File[] files = toFiles(driverFiles);
		URLClassLoader loader = getURLClassLoader(files);
		for (int i = 0, length = files.length; i < length; i++) {
            try {
                JarFile jar = new JarFile(files[i]);
                addCandidateDriversToList(list, loader, jar);
            }
            catch (IOException e) {
                throw new SystemException(e);
            }
		}
		return list;
	}
	
	/*
	 * @param driverFiles file paths
	 * @return an array of File objects corresponding to the given file paths. 
     * Only valid files are included
	 */
	private static File[] toFiles(String[] driverFiles) {
		List<File> list = new ArrayList<File>();
		
		for (int i=0, length=driverFiles.length; i < length; i++) {
			File file = new File(driverFiles[i]);
			if (file.exists() && file.isFile()) {
				list.add(file);
			}
		}
		return list.toArray(new File[list.size()]);
	}

	@SuppressWarnings("unchecked")
	private static void addCandidateDriversToList(
            List<Class<Driver>> list, URLClassLoader loader, JarFile jar)  {
        
		for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements(); ) {
			JarEntry entry = (JarEntry) e.nextElement();
			String className = getClassNameFromFileName(entry.getName());
			if (className != null) {
                try {
                    Class<?> driverClass = loader.loadClass(className);
                    if (Driver.class.isAssignableFrom(driverClass))
                        list.add((Class<Driver>)driverClass);
                }
                catch (UnsupportedClassVersionError ex) {
                }
                catch (ClassNotFoundException ex) {
                }
                catch (NoClassDefFoundError ex) {
                }
			}
		}
	}

	private static String getClassNameFromFileName(String name) {
		String result = null;
		if (name.endsWith(".class")) {
			result = name.substring(0, name.length()-6).replace('/', '.').replace('\\', '.' );
		}
		return result;
	}

	/**
	 * @param files files that make up a classpath
	 * @return a URLClassLoader that loads from the given files
	 * @throws MalformedURLException
	 */
	private static URLClassLoader getURLClassLoader(File[] files) throws SystemException {
		
		String driverPath = getFilePath(files);
		URLClassLoader loader = (URLClassLoader)classLoaderCache.get(driverPath);
		if (loader == null) {
		    URL urls[] = new URL[files.length];
		    for (int i = 0, length = urls.length; i < length; i++) {
			    try {
                    urls[i] = files[i].toURI().toURL();
                }
                catch (MalformedURLException e) {
                    throw new SystemException(e);
                }
			}
		    loader = new URLClassLoader(urls);
            classLoaderCache.put(driverPath, loader);
		}
		return loader;
	}

	/**
	 * @param files files whose path should be concatenated
	 * @return the concatenated path, using File.pathSeparator
	 */
	private static String getFilePath(File[] files) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = files == null ? 0 : files.length; i < length; i++) {
			buffer.append(files[i].getAbsolutePath());
			if (i < length-1) {
				buffer.append(File.pathSeparator);
			}
		}
		return buffer.toString();
	}
}
