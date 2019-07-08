package com.yzbzz.media.library.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class FileUtils {

    private static final String SEPARATOR = File.separator;

    public static void copyFile(File sourcefile, File targetFile) throws IOException {

        FileInputStream input = new FileInputStream(sourcefile);

        FileOutputStream out = new FileOutputStream(targetFile);
        BufferedOutputStream outbuff = new BufferedOutputStream(out);

        byte[] b = new byte[1024];
        int len;
        while ((len = input.read(b)) != -1) {
            outbuff.write(b, 0, len);
        }

        outbuff.close();
        input.close();

    }

    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {

        (new File(targetDir)).mkdirs();

        File[] file = (new File(sourceDir)).listFiles();

        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                File sourceFile = file[i];
                File targetFile = new File(targetDir + File.separator + sourceFile.getName());
                copyFile(sourceFile, targetFile);

            }

            if (file[i].isDirectory()) {
                String dir1 = sourceDir + File.separator + file[i].getName();
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
        if (file.isDirectory()) {
            if (isDeleteFile) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    deleteFile(f, filterName);
                }
            }
            // file.delete();
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
            // file.delete();
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

    public static boolean isExitsSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isFileExists(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        return new File(path).exists();
    }

    private static void createDirectory(String path) {
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void saveFile(String url, String content) {
        saveFile(url, content, true, false);
    }

    public static void saveFile(String url, String content, boolean cover, boolean append) {
        FileOutputStream out = null;
        File file = new File(url);

        try {
            if (file.exists()) {
                if (cover) {
                    file.delete();
                    file.createNewFile();
                }
            } else {
                file.createNewFile();
            }

            out = new FileOutputStream(file, append);
            out.write(content.getBytes());
            out.close();
        } catch (Exception e) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void deleteFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);

            if (file.exists()) {
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteRead;

            File oldFile = new File(oldPath);
            File newFile = new File(newPath);

            if (oldFile.exists()) { //文件存在时
                if (newFile.exists()) {
                    newFile.delete();
                }

                newFile.createNewFile();

                FileInputStream inputStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream outputStream = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];

                while ((byteRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, byteRead);
                }

                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FileInputStream getFileInputStreamFromFile(String fileUrl) {
        FileInputStream fileInputStream = null;

        try {
            File file = new File(fileUrl);

            fileInputStream = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileInputStream;
    }

    public static FileOutputStream getFileOutputStreamFromFile(String fileUrl) {
        FileOutputStream bufferedOutputStream = null;

        try {
            File file = new File(fileUrl);

            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            bufferedOutputStream = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bufferedOutputStream;
    }

    public static BufferedOutputStream getBufferedOutputStreamFromFile(String fileUrl) {
        BufferedOutputStream bufferedOutputStream = null;

        try {
            File file = new File(fileUrl);

            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bufferedOutputStream;
    }

    public static void renameFile(String oldPath, String newPath) {
        if (!TextUtils.isEmpty(oldPath) && !TextUtils.isEmpty(newPath)) {
            File newFile = new File(newPath);

            if (newFile.exists()) {
                newFile.delete();
            }

            File oldFile = new File(oldPath);

            if (oldFile.exists()) {
                try {
                    oldFile.renameTo(new File(newPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copyFilesFromRaw(Context context, int id, String outputPath, String fileName) {
        InputStream inputStream = context.getResources().openRawResource(id);
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        readInputStream(outputPath + SEPARATOR + fileName, inputStream);
    }

    public static void readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[inputStream.available()];
                int lenght;
                while ((lenght = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
