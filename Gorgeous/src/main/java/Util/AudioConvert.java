package Util;

import cn.hutool.core.io.IoUtil;
import org.bytedeco.javacv.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

public class AudioConvert {
    boolean Mp3ToOpus(String srcPath, String destPath) {
        SeekableByteArrayOutputStream byteArrayOutputStream = new SeekableByteArrayOutputStream() {
            @Override
            public synchronized String toString(){
                return "voice.opus";
            }
        };

        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(IoUtil.toStream(new File(srcPath)))) {
            frameGrabber.start();
            FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(byteArrayOutputStream, 1);
            frameRecorder.setSampleFormat(48000);
            frameRecorder.start();
            Frame frame;
            while ((frame = frameGrabber.grabSamples()) != null) {
                frameRecorder.record(frame);
            }
            frameGrabber.stop();
            frameRecorder.stop();

            FileOutputStream fileOutputStream = new FileOutputStream(destPath);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            return true;
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
