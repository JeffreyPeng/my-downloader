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

    public long getLength() {
        return length;
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
        console = System.console();
        startTime = System.currentTimeMillis();
        new Thread(this).start();
    }
    Console console;
    long lastSpeed = 0;
    long lastPos = 0;
    long startTime = 0;

    @Override
    public void run() {
        try {
            while (true) {
                long pos = downloader.getPos();
                long length = downloader.getLength();
                long percent = pos * 100 /length;
                String downloadedSize = CalcUtil.ByteToShow(pos);
                String speed = "";
                long newSpeed = pos - lastPos;
                if (lastSpeed == 0) {
                    speed = CalcUtil.ByteToShow(newSpeed) + "/s";
                } else {
                    newSpeed = lastSpeed + (newSpeed - lastSpeed) / 2;
                    speed = CalcUtil.ByteToShow(newSpeed) + "/s";
                }
                lastSpeed = newSpeed;
                lastPos = pos;
                String timsShow = "unknow";
                if (percent == 100) {
                    timsShow = "in " + CalcUtil.SecondToShow((System.currentTimeMillis() - startTime) / 1000);
                } else {
                    if (newSpeed != 0) {
                        timsShow = "eta " + CalcUtil.SecondToShow((length - pos) / newSpeed);
                    }
                }
                if (console != null) {
                    console.writer().write("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b"); // 40
                    console.writer().write("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b"); // 40
                    console.writer().write("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b"); // 40
                    console.writer().printf("progress: % 4d%%", percent);
                    String template1 = "================================>";
                    String template2 = "                                 ";
                    int slot = 33;
                    int index = slot * (int)percent / 100;
                    console.writer().printf("[");
                    if (index == 0) {
                        console.writer().printf(">                                ");
                    } else if (index == slot) {
                        console.writer().printf("================================>");
                    } else {
                        console.writer().printf(template1.substring(0, index));
                        console.writer().printf(">");
                        console.writer().printf(template2.substring(0, slot - index));
                    }
                    console.writer().printf("]");
                    console.writer().printf("%8s", downloadedSize);
                    console.writer().printf("%11s", speed);
                    console.writer().printf("%11s", timsShow);
                    console.writer().printf("   ");
                } else {
                    System.out.println(String.format("progress: % 4d%%%8s%11s%11s", percent, downloadedSize, speed, timsShow));
                }

                if (percent == 100) {
                    if (console != null) {
                        console.writer().println();
                    } else {
                        System.out.println();
                    }
                    break;
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}