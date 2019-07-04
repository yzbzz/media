package com.yzbzz.media.library.utils;

import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class FileUtils {

    public static void copyAllFiles(File files, File fileCopy) {
        try {
            //判断是否是文件
            if (files.isDirectory()) {
                // 如果不存在，创建文件夹
                if (!fileCopy.exists()) {
                    fileCopy.mkdir();
                }
                // 将文件夹下的文件存入文件数组
                String[] fs = files.list();
                for (String f : fs) {
                    //创建文件夹下的子目录
                    File srcFile = new File(files, f);
                    File destFile = new File(fileCopy, f);
                    // 将文件进行下一层循环
                    copyAllFiles(srcFile, destFile);
                }
            } else {


                // 创建文件输入的字节流用于读取文件内容，源文件
                FileInputStream fis = new FileInputStream(files);

                // 创建文件输出的字节流，用于将读取到的问件内容写到另一个磁盘文件中，目标文件
                FileOutputStream os = new FileOutputStream(fileCopy);

                // 创建字符串，用于缓冲

                int len = -1;
                byte[] b = new byte[1024];
                while (true) {
                    // 从文件输入流中读取数据。每执行一次,数据读到字节数组b中
                    len = fis.read(b, 0, 256);
                    if (len == -1) {
                        break;
                    }
                    System.out.println(b.toString());
                    os.write(b);
                }
                os.write("\r\n".getBytes()); // 换行
                fis.close();
                os.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void deleteFile(File file, String filterName) {
        String fileName = file.getName();
        if (file.isDirectory() && !fileName.equalsIgnoreCase(filterName)) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f, filterName);
            }
            // file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            // file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    public static void writeAudioInfo(String fileName, List<String> items) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

        BufferedWriter bufferedWriter = null;
        try {
            file.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(file));

            StringBuilder stringBuilder = new StringBuilder();
            for (String item : items) {
                stringBuilder.append("file ");
                stringBuilder.append(item);
                stringBuilder.append("\n");
            }

            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedWriter) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 确保目录存在,没有则创建
     */
    public static boolean confirmFolderExist(String folderPath) {

        File file = new File(folderPath);
        if (!file.exists()) {
            return file.mkdirs();
        }

        return false;
    }

    public static boolean checkFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        return new File(filePath).exists();
    }

    /**
     * 重命名
     */
    public static File renameFile(File srcFile, String newName) {

        File destFile = new File(newName);
        srcFile.renameTo(destFile);

        return destFile;
    }

}
