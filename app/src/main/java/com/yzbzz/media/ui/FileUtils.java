package com.yzbzz.media.ui;

import java.io.File;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class FileUtils {

    public   static void deleteFile(File file) {
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
}
