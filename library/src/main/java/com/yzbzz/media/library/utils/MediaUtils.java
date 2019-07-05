package com.yzbzz.media.library.utils;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.media.MediaPlayer;
import android.net.Uri;

import com.yzbzz.media.library.bean.Audio;
import com.yzbzz.media.library.bean.AudioEntity;
import com.yzbzz.media.library.common.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class MediaUtils {

    public static List<AudioEntity> cutAudios(String sdcardPath, String srcPath, List<AudioEntity> audioEntities, String recordPath, String blankPath) {

        String outName = "dest" + Constant.SUFFIX_WAV;

        //裁剪后音频的路径
        String destPath = sdcardPath + outName;

        //解码源音频，得到解码后的文件
        decodeAudio(srcPath, destPath);

        if (!FileUtils.checkFileExist(destPath)) {
            return null;
        }

        Audio audio = getAudioFromPath(destPath);

        if (audio != null) {
            int size = audioEntities.size();
            int recodeCount = 1;
            int blankCount = 1;

            DecimalFormat decimalFormat = new DecimalFormat("000");//确定格式，把1转换为001
            String suffix;

            for (int i = 0; i < size; i++) {
                AudioEntity audioEntity = audioEntities.get(i);
                String path;
                if (audioEntity.canRead) {
                    suffix = "u_00" + decimalFormat.format(recodeCount);
                    path = recordPath;
                    recodeCount++;
                } else {
                    path = blankPath;
                    suffix = "b_00" + decimalFormat.format(blankCount);
                    blankCount++;
                }
                audioEntity.path = AudioEditUtil.cutAudio(path, audio, suffix, audioEntity.beginTime, audioEntity.endTime);
            }
        }
        return audioEntities;
    }

    public static void decodeAudio(String path, String destPath) {
        final File file = new File(path);

        if (FileUtils.checkFileExist(destPath)) {
            FileUtils.deleteFile(new File(destPath));
        }

        FileUtils.confirmFolderExist(new File(destPath).getParent());

        DecodeEngine.getInstance().convertMusicFileToWaveFile(path, destPath);
    }

    private static Audio getAudioFromPath(String path) {
        if (!FileUtils.checkFileExist(path)) {
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Audio audio = Audio.createAudioFromFile(new File(path));
                return audio;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 获取时间长度
     */
    public static int getFilePlayTime(Context context, File file) {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(file.toString()));
            //使用Date格式化播放时间mediaPlayer.getDuration()
            int duration = mediaPlayer.getDuration();
            mediaPlayer.release();
            return duration;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void exactorMedia(String input, String outputVideo, String outputAudio) {
        MediaExtractor mediaExtractor = new MediaExtractor();

        FileOutputStream videoOutputStream = null;
        FileOutputStream audioOutputStream = null;
        try {

            File videoFile = new File(outputVideo);
            if (!videoFile.exists()) {
                videoFile.createNewFile();
            }

            File audioFile = new File(outputAudio);
            if (!audioFile.exists()) {
                audioFile.createNewFile();
            }


            videoOutputStream = new FileOutputStream(videoFile);
            audioOutputStream = new FileOutputStream(audioFile);


            mediaExtractor.setDataSource(input);

            int trackCount = mediaExtractor.getTrackCount();

            int audioTrackIndex = -1;
            int videoTrackIndex = -1;
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                if (mineType.startsWith("video/")) {
                    videoTrackIndex = i;
                }

                if (mineType.startsWith("audio/")) {
                    audioTrackIndex = i;
                }
            }


            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);

            // 切换到视频信道
            mediaExtractor.selectTrack(videoTrackIndex);
            while (mediaExtractor.readSampleData(byteBuffer, 0) >= 0) {

                int readSampleCount = mediaExtractor.getSampleTrackIndex();

                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                videoOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }

            // 切换到音频信道
            mediaExtractor.selectTrack(audioTrackIndex);
            while (mediaExtractor.readSampleData(byteBuffer, 0) >= 0) {
                int readSampleCount = mediaExtractor.getSampleTrackIndex();

                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                audioOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaExtractor.release();
            try {
                videoOutputStream.close();
                audioOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 分离视频的视频轨，输入视频 input.mp4，输出视频 output_video.mp4
    public static void extractVideo(String input, String output) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        MediaMuxer mediaMuxer = null;
        try {
            // 设置视频源
//            FileInputStream fileInputStream = new FileInputStream(input);
//            mediaExtractor.setDataSource(fileInputStream.getFD());
            mediaExtractor.setDataSource(input);
            // 轨道索引 ID
            int videoIndex = -1;
            // 视频轨道格式信息
            MediaFormat mediaFormat = null;
            // 数据源的轨道数（一般有视频，音频，字幕等）
            int trackCount = mediaExtractor.getTrackCount();
            // 循环轨道数，找到我们想要的视频轨
            for (int i = 0; i < trackCount; i++) {
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mimeType = format.getString(MediaFormat.KEY_MIME);
                // //找到要分离的视频轨
                if (mimeType.startsWith("video/")) {
                    videoIndex = i;
                    mediaFormat = format;
                    break;
                }
            }
            if (mediaFormat == null) {
                return;
            }

            // 最大缓冲区字节数
            int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            // 格式类型
            String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
//            // 视频的比特率
//            int bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE);
//            // 视频宽度
//            int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
//            // 视频高度
//            int height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
//            // 内容持续时间（以微妙为单位）
//            long duration = mediaFormat.getLong(MediaFormat.KEY_DURATION);
//            // 视频的帧率
//            int frameRate = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
//            // 视频内容颜色空间
//            int colorFormat = -1;
            if (mediaFormat.containsKey(MediaFormat.KEY_COLOR_FORMAT)) {
                mediaFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
            }
            // 关键之间的时间间隔
            int iFrameInterval = -1;
            if (mediaFormat.containsKey(MediaFormat.KEY_I_FRAME_INTERVAL)) {
                iFrameInterval = mediaFormat.getInteger(MediaFormat.KEY_I_FRAME_INTERVAL);
            }
            //  视频旋转顺时针角度
            int rotation = -1;
            if (mediaFormat.containsKey(MediaFormat.KEY_ROTATION)) {
                rotation = mediaFormat.getInteger(MediaFormat.KEY_ROTATION);
            }
            // 比特率模式
            int bitRateMode = -1;
            if (mediaFormat.containsKey(MediaFormat.KEY_BITRATE_MODE)) {
                bitRateMode = mediaFormat.getInteger(MediaFormat.KEY_BITRATE_MODE);
            }

            //切换视频的轨道
            mediaExtractor.selectTrack(videoIndex);

            mediaMuxer = new MediaMuxer(output, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            //将视频轨添加到 MediaMuxer，并返回新的轨道
            int trackIndex = mediaMuxer.addTrack(mediaFormat);
            ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            // 开始合成
            mediaMuxer.start();
            while (true) {
                // 检索当前编码的样本并将其存储在字节缓冲区中
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                //  如果没有可获取的样本则退出循环
                if (readSampleSize < 0) {
                    mediaExtractor.unselectTrack(videoIndex);
                    break;
                }
                // 设置样本编码信息
                bufferInfo.size = readSampleSize;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                //写入样本数据
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);
                //推进到下一个样本，类似快进
                mediaExtractor.advance();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMuxer != null) {
                mediaMuxer.stop();
                mediaMuxer.release();
            }
            mediaExtractor.release();
        }
    }

    // 分离视频的音频轨, 输入视频 input.mp4, 输出的音频 output_audio.mp3
    public static void extractAudio(String input, String output) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        MediaMuxer mediaMuxer = null;
        try {
//            FileInputStream fileInputStream = new FileInputStream(input);
//            mediaExtractor.setDataSource(fileInputStream.getFD());
            mediaExtractor.setDataSource(input);
            int trackCount = mediaExtractor.getTrackCount();
            MediaFormat mediaFormat = null;
            int audioIndex = -1;
            for (int i = 0; i < trackCount; i++) {
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mimeType = format.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("audio/")) {
                    audioIndex = i;
                    mediaFormat = format;
                    break;
                }
            }
            if (mediaFormat == null) {
                return;
            }
            // MediaFormat 封装了媒体数据（音频，视频，字幕）格式的信息，所有信息都以键值对形式表示。
            // MediaFormat 中定义的 key 对于不同媒体数据并不是全部通用的，某些 key 只适用于特定媒体数据。
            // 最大缓冲区字节数
            int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            // 格式
            String mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
            // 比特率
            int bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE);
            // 通道数
            int channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            // 采样率
            int sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            // 内容持续时间（以微妙为单位）
            long duration = mediaFormat.getLong(MediaFormat.KEY_DURATION);

            mediaExtractor.selectTrack(audioIndex);

            mediaMuxer = new MediaMuxer(output, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeAudioIndex = mediaMuxer.addTrack(mediaFormat);
            ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();
            while (true) {
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    mediaExtractor.unselectTrack(audioIndex);
                    break;
                }
                bufferInfo.size = readSampleSize;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.offset = 0;
                bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo);
                mediaExtractor.advance();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mediaMuxer != null) {
                mediaMuxer.stop();
                mediaMuxer.release();
            }
            mediaExtractor.release();
        }
    }

    // 将上面分离出的 output_video.mp4 和分离出 output_audio.mp4 合成原来完整的视频
    // 输入视频 output_video.mp4，输入音频 output_audio.mp4 合成视频 output.mp4
    public static void combineVideo(String outputVideo, String outputAudio, String outPath) {
        MediaExtractor videoExtractor = new MediaExtractor();
        MediaExtractor audioExtractor = new MediaExtractor();
        MediaMuxer mediaMuxer = null;
        try {
            videoExtractor.setDataSource(outputVideo);
            MediaFormat videoFormat = null;
            int videoTrackIndex = -1;
            int videoTrackCount = videoExtractor.getTrackCount();
            for (int i = 0; i < videoTrackCount; i++) {
                MediaFormat format = videoExtractor.getTrackFormat(i);
                String mimeType = format.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("video/")) {
                    videoTrackIndex = i;
                    videoFormat = format;
                    break;
                }
            }
            if (videoFormat == null) {
                return;
            }

            audioExtractor.setDataSource(outputAudio);
            MediaFormat audioFormat = null;
            int audioTrackIndex = -1;

            int audioTrackCount = audioExtractor.getTrackCount();
            for (int i = 0; i < audioTrackCount; i++) {
                MediaFormat format = audioExtractor.getTrackFormat(i);
                String mimeType = format.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i;
                    audioFormat = format;
                    break;
                }
            }
            if (audioFormat == null) {
                return;
            }

            mediaMuxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeVideoTrackIndex = mediaMuxer.addTrack(videoFormat);
            int writeAudioTrackIndex = mediaMuxer.addTrack(audioFormat);
            mediaMuxer.start();

            videoExtractor.selectTrack(videoTrackIndex);
            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            int videoMaxInputSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            ByteBuffer videoByteBuffer = ByteBuffer.allocate(videoMaxInputSize);
            while (true) {
                int readVideoSampleSize = videoExtractor.readSampleData(videoByteBuffer, 0);
                if (readVideoSampleSize < 0) {
                    videoExtractor.unselectTrack(videoTrackIndex);
                    break;
                }
                videoBufferInfo.size = readVideoSampleSize;
                videoBufferInfo.presentationTimeUs = videoExtractor.getSampleTime();
                videoBufferInfo.offset = 0;
                videoBufferInfo.flags = videoExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeVideoTrackIndex, videoByteBuffer, videoBufferInfo);
                videoExtractor.advance();
            }

            audioExtractor.selectTrack(audioTrackIndex);
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
            int audioMaxInputSize = audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            ByteBuffer audioByteBuffer = ByteBuffer.allocate(audioMaxInputSize);
            while (true) {
                int readAudioSampleSize = audioExtractor.readSampleData(audioByteBuffer, 0);
                if (readAudioSampleSize < 0) {
                    audioExtractor.unselectTrack(audioTrackIndex);
                    break;
                }
                audioBufferInfo.size = readAudioSampleSize;
                audioBufferInfo.presentationTimeUs = audioExtractor.getSampleTime();
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = audioExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeAudioTrackIndex, audioByteBuffer, audioBufferInfo);
                audioExtractor.advance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMuxer != null) {
                mediaMuxer.release();
            }
            videoExtractor.release();
            audioExtractor.release();
        }
    }

    public static String getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mediaMetadataRetriever.release();
        return durationStr;
    }

    public static long getWavDuration(String sourceFile) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        MediaFormat mediaFormat;

        try {
            mediaExtractor.setDataSource(sourceFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                mediaExtractor.setDataSource(new FileInputStream(sourceFile).getFD());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mediaFormat = mediaExtractor.getTrackFormat(0);
        long duration = mediaFormat.containsKey(MediaFormat.KEY_DURATION) ? mediaFormat.getLong(MediaFormat.KEY_DURATION) : 0;
        mediaExtractor.release();
        return duration;
    }
}
