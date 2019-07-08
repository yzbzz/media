# Media
一个处理音视频的库(分离音视频，切割音频，合成音频、视频)，封装了FFmpeg常用音视频命令和Android原生类处理音视频



## FFmpeg相关类

##### 主要使用`FFmpegUtils`类和`FFmpegCmdUtils`类

##### FFmpegUtils类封装了加载FFmpeg和执行FFmpeg命令的操作，相关方法声明如下：

```java
// 加载FFmpeg，需要在Application的onCreate中执行 
public static void load(Context context,final Callback<String> callback)
  
// 执行FFmpeg命令
public static void executeCmd(Context context, String[] cmd, final Callback<String> callback)
```

##### FFmpegCmdUtils类封装了常用的`FFmpeg`命令，返回一个String[]，使用FFmpegUtils执行，部分方法声明如下：

```java
// 提取音频
public static String[] extractorAudio(String srcFile, String targetFile)
// 提取视频
public static String[] extractorVideo(String srcFile, String targetFile)
```



## Android原生操作

#### 相关使用类

- MediaUtils  该类主要封装了分享音视频，切割音视频
- DecodeEngine  该类主要封装了解码文件等操作
- AudioEncodeUtil 该类主要是用于PCW和WAV等格式的互转操作

##### MediaUtils部分声明

```java
// 分离音频
public static void extractAudio(String input, String output)

// 分离视频
public static void extractVideo(String input, String output)

// 切割音频
public static List<AudioEntity> cutAudios(String sdcardPath, String srcPath, ...)
```

##### DecodeEngine部分声明

```java
// 解码文件
private void getDecodeData(MediaExtractor mediaExtractor, MediaCodec mediaCodec, ...)
```

##### AudioEncodeUtil部分声明

```java
// wav转pcm
public static void convertWav2Pcm(String inWaveFilePath, String outPcmFilePath)
  
// pcm转acc
public static void convertPcm2Acc(String inPcmFilePath, String outAccFilePath)
  
// pcm转wav
public static void convertPcm2Wav(String inPcmFilePath, String outWavFilePath)
```

更多操作详见Demo



## Download

使用`Gradle`进行引用

1.在你根目录的`build.gradle`文件添加代码: `maven { url 'https://www.jitpack.io' }`

```
allprojects {
    repositories {
        ...
        maven { url 'https://www.jitpack.io' }
    }
}
```

2.在你工程的`build.gradle`添加

工程依赖`FFmpegAndroid`，所以需要同时引用`FFmpegAndroid`包

```
dependencies {
    implementation 'com.github.yzbzz:media:1.0.1'
    implementation 'com.writingminds:FFmpegAndroid:0.3.2'
}
```

你也可以直接下载`library`库放到你的工程中
