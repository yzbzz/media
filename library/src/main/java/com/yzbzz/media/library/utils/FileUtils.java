package com.yzbzz.media.library.utils;

import android.text.TextUtils;

import java.io.BufferedOutputStream;
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

    public static void copyFile(File sourcefile, File targetFile) throws IOException {

        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourcefile);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream out = new FileOutputStream(targetFile);
        BufferedOutputStream outbuff = new BufferedOutputStream(out);

        // 缓冲数组
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = input.read(b)) != -1) {
            outbuff.write(b, 0, len);
        }

        //关闭文件
        outbuff.close();
        input.close();

    }

    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {

        // 新建目标目录
        (new File(targetDir)).mkdirs();

        // 获取源文件夹当下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();

        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(targetDir + File.separator + sourceFile.getName());
                copyFile(sourceFile, targetFile);

            }

            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + File.separator + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + File.separator + file[i].getName();

                copyDirectiory(dir1, dir2);
            }
        }

    }

    public static void deleteFile(File file, String... filterName) {
        String fileName = file.getName();
        boolean isDeleteFile = true;
        for (String name : filterName) {
            if (fileName.equalsIgnoreCase(name)) {
                isDeleteFile = false;
                break;
            }
        }
        if (file.isDirectory() && isDeleteFile) {
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
