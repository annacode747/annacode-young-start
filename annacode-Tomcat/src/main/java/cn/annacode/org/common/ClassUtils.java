package cn.annacode.org.common;

import cn.annacode.org.Tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {


    /**
     * 获取class名称
     *
     * @param file           文件
     * @param classDirectory 工作路径
     * @return String
     */
    public static String getClassName(File file, File classDirectory) {
        String filePath = file.getPath();
        String classPath = classDirectory.getPath();
        return filePath.replace(classPath + File.separator, "")
                .replace(".class", "")
                .replace(File.separator, ".");
    }

    /**
     * 获取一个文件夹内所有的文件 包括子文件夹
     *
     * @param srcFile 文件
     * @return 文件列表
     */
    public static List<File> getAllFilePath(File srcFile) {
        List<File> result = new ArrayList<>();
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    result.add(file);
                } else if (file.isDirectory()) {
                    result.addAll(getAllFilePath(file));
                }
            }
        }
        return result;
    }


    /**
     * 获取文件列表
     *
     * @param folder 文件
     * @return 文件列表
     */
    public static ArrayList<String> getSubFolderNames(File folder) {
        ArrayList<String> subFolderNames = new ArrayList<String>();
        File[] filesAndFolders = folder.listFiles();

        if (filesAndFolders == null)
            return subFolderNames;
        for (File file : filesAndFolders) {
            if (file.isDirectory()) {
                subFolderNames.add(file.getName());
                subFolderNames.addAll(getSubFolderNames(file));
            }
        }

        return subFolderNames;
    }

    public static List<Pair<File, String>> getDirectoriesWithClassess(File rootDirectory, String classe) {
        List<Pair<File, String>> directories = new ArrayList<>();
        File[] files = rootDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果该文件夹下有classess文件夹，则加入结果集
                    File classDirectory = new File(file, classe);
                    if (classDirectory.exists() && classDirectory.isDirectory()) {
                        String apps = new Tomcat().getApps();
                        String name = rootDirectory.getPath()
                                .substring(
                                        rootDirectory.getPath().indexOf(apps + File.separator) + ((apps + File.separator).length())
                                );
                        int index = name.indexOf(File.separator);
                        if (index > 0)
                            name = name.substring(0, name.indexOf(File.separator));
                        directories.add(new Pair<>(file, name));
                        System.out.println(name);

                    }
                    // 递归调用
                    directories.addAll(getDirectoriesWithClassess(file, classe));
                }
            }
        }
        return directories;
    }
}
