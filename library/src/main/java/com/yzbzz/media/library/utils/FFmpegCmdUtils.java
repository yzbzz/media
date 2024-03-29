package com.yzbzz.media.library.utils;

import java.util.List;

/**
 * Created by yzbzz on 2019-07-02.
 */
public class FFmpegCmdUtils {

    /**
     * 提取 AAC 音频
     *
     * @param srcFile    源文件
     * @param targetFile 输出文件
     * @return ffmpeg命令
     */
    public static String[] extractorAudioByAAC(String srcFile, String targetFile) {
        String extractAudioCmd = "-i %s -map 0:1 -acodec copy -y %s";
        extractAudioCmd = String.format(extractAudioCmd, srcFile, targetFile);
        String[] audioCmd = extractAudioCmd.split(" ");
        return audioCmd;
    }

    /**
     * 提取 Mp3 音频
     *
     * @param srcFile    源文件
     * @param targetFile 输出文件
     * @return ffmpeg命令
     */
    public static String[] extractorAudioByMp3(String srcFile, String targetFile) {
        String extractAudioCmd = "-i %s -map 0:2 -acodec copy -y %s";
        extractAudioCmd = String.format(extractAudioCmd, srcFile, targetFile);
        String[] audioCmd = extractAudioCmd.split(" ");
        return audioCmd;
    }

    /**
     * 提取音频
     *
     * @param srcFile    源文件
     * @param targetFile 输出文件
     * @return ffmpeg命令
     */
    public static String[] extractorAudio(String srcFile, String targetFile) {
        String extractAudioCmd = "-i %s -acodec copy -vn %s";
        extractAudioCmd = String.format(extractAudioCmd, srcFile, targetFile);
        String[] audioCmd = extractAudioCmd.split(" ");
        return audioCmd;
    }

    /**
     * 提取视频
     *
     * @param srcFile    源文件
     * @param targetFile 输出文件
     * @return ffmpeg命令
     */
    public static String[] extractorVideo(String srcFile, String targetFile) {
        String extractVideoCmd = "-i %s -vcodec copy -an %s";
        extractVideoCmd = String.format(extractVideoCmd, srcFile, targetFile);
        String[] videoCmd = extractVideoCmd.split(" ");
        return videoCmd;
    }

    /**
     * 通过步长截取音频（从 xx 秒开始，截取多长时间的音频）
     *
     * @param srcFile      源文件
     * @param beginTime    开始时长
     * @param durationTime 截取多长时间
     * @param outputFile
     * @return ffmpeg命令
     */
    public static String[] cutAudioByDuration(String srcFile, String beginTime, String durationTime, String outputFile) {
        // ffmpeg -i output_audio.mp3 -ss 00:00:02 -to 00:00:05 -y clip111.mp3
        String extractAudioCmd = "-i %s -vn -acodec copy -ss %s -t %s %s";
        extractAudioCmd = String.format(extractAudioCmd, srcFile, beginTime, durationTime, outputFile);
        String[] audioCmd = extractAudioCmd.split(" ");
        return audioCmd;
    }

    /**
     * 截取音频（从 xx 秒到 xx 秒）
     *
     * @param srcFile
     * @param beginTime
     * @param endTime
     * @param outputFile
     * @return ffmpeg命令
     */
    public static String[] cutAudio(String srcFile, String beginTime, String endTime, String outputFile) {
        // ffmpeg -ss 00:01:00 -i video.mp4 -to 00:02:00 -c copy -copyts cut.mp4
        String extractAudioCmd = "-ss %s -i %s -to %s -c copy -copyts %s";
        extractAudioCmd = String.format(extractAudioCmd, beginTime, srcFile, endTime, outputFile);

        String[] audioCmd = extractAudioCmd.split(" ");
        return audioCmd;
    }

    /**
     * 拼接音频文件
     *
     * @param file       音频文件列表
     * @param targetFile 输出文件
     * @return ffmpeg命令
     */
    public static String[] concatAudiosByFile(String file, String targetFile) {
        // ffmpeg -f concat -safe 0 -i mylist.txt -c copy -y combineall.mp3
        String cmd = "-f concat -safe 0 -i %s -c copy -y %s";
        cmd = String.format(cmd, file, targetFile);
        return cmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 拼接音频文件
     *
     * @param srcFiles   音频文件列表
     * @param targetFile 输出文件
     * @return ffmpeg命令
     */
    public static String[] concatAudios(List<String> srcFiles, String targetFile) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-i").append(" concat:");

        int size = srcFiles.size();
        for (int i = 0; i < size; i++) {
            String item = srcFiles.get(i);

            stringBuilder.append(item);
            if (i < size - 1) {
                stringBuilder.append("|");
            }
        }

        stringBuilder.append(" -acodec");
        stringBuilder.append(" copy ");
        stringBuilder.append(targetFile);
        String concatAudioCmd = stringBuilder.toString();
        return concatAudioCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 拼接2个音频
     *
     * @param srcFile    第一个音频文件
     * @param appendFile 第二个音频文件
     * @param targetFile 输出文件
     * @return ffmpeg命令
     */
    public static String[] concatAudio(String srcFile, String appendFile, String targetFile) {
        String concatAudioCmd = "-i concat:%s|%s -acodec copy %s";
        concatAudioCmd = String.format(concatAudioCmd, srcFile, appendFile, targetFile);
        return concatAudioCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 文件转换
     *
     * @param srcFile
     * @param targetFile
     * @return
     */
    public static String[] convertedFile(String srcFile, String targetFile) {
        String convertedFileCmd = "-y -i %s %s";
        convertedFileCmd = String.format(convertedFileCmd, srcFile, targetFile);
        return convertedFileCmd.split(" ");
    }

    public static String[] combineMedia(String videoFile, String audioFile, String outputFile) {
        // -i 1.mp4 -i 1.mp3 -vcodec copy -acodec copy 1.mp4
        String convertedFileCmd = "-i %s -i %s -vcodec copy -acodec copy %s";
        convertedFileCmd = String.format(convertedFileCmd, videoFile, audioFile, outputFile);
        return convertedFileCmd.split(" ");
    }
}
