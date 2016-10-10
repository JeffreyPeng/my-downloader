import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by pen-tpc on 2016/10/8.
 */
public class ConsoleDownloader {

    public static final String url = "http://dldir1.qq.com/qqfile/QQforMac/QQ_V5.1.2.dmg";
    public static final String filePath = "D:/QQ_V5.1.2.dmg";

    private long pos;
    private long length;

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public static void main(String[] args) {
        new ConsoleDownloader().download();
    }
    public void download() {
        try {
            URL downloadUrl = new URL(url);
            URLConnection urlConnection = downloadUrl.openConnection();
            urlConnection.connect();
            length = urlConnection.getContentLengthLong();
            InputStream inputStream = urlConnection.getInputStream();
            byte []bytes = new byte[1024*8];
            int readCount = inputStream.read(bytes);
            FileOutputStream outputStream = new FileOutputStream(filePath);
            pos = 0;
            new ConsolePrinter(this).startPrint();
            while (readCount != -1) {
                outputStream.write(bytes, 0, readCount);
                pos += readCount;
                readCount = inputStream.read(bytes);
            }
            outputStream.close();
            inputStream.close();
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
class ConsolePrinter implements Runnable {
    public ConsolePrinter(ConsoleDownloader downloader) {
        this.downloader = downloader;
    }

    private ConsoleDownloader downloader;

    public void startPrint() {
        new Thread(this).start();
        console = System.console();
    }
    Console console;
    long lastSpeed = 0;
    long lastPos = 0;

    @Override
    public void run() {
        try {
            while (true) {
                long pos = downloader.getPos();
                long length = downloader.getLength();
                long percent = pos * 100 /length;
                String downloadedSize = byteToShow(pos);
                String speed = "";
                long newSpeed = pos - lastPos;
                if (lastSpeed == 0) {
                    speed = byteToShow(newSpeed) + "/s";
                } else {
                    newSpeed = lastSpeed + (newSpeed - lastSpeed) / 2;
                    speed = byteToShow(newSpeed) + "/s";
                }
                lastSpeed = newSpeed;
                lastPos = pos;
                String needTime = "eta 41s";
                String totalTime = "in 43s";
                if (console != null) {
                    console.writer().write("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b"); // 40
                    console.writer().write("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b"); // 40
                    console.writer().printf("progress: % 4d%%", percent);
                    console.writer().printf("[================================>]");
                    console.writer().printf("% 8s", downloadedSize);
                    console.writer().printf("% 10s", speed);
                    console.writer().printf("% 11s", needTime);
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public String byteToShow(long bytes) {
        if (bytes < 1000L) {
            return bytes + "B";
        } else if (bytes < 1000_000) {
            return String.format("%.2fKB", bytes / 1000.0);
        } else if (bytes < 1000_000_000) {
            return String.format("%.2fMB", bytes / 1000_000.0);
        } else {
            return String.format("%.2fGB", bytes / 1000_000_000.0);
        }
    }
}