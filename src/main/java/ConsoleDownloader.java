import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by pen-tpc on 2016/10/8.
 */
public class ConsoleDownloader {

    public static final String url = "http://down.sandai.net/thunder9/Thunder9.0.16.408.exe";
    public static final String filePath = "D:/Thunder9.0.16.408.exe";

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
            byte []bytes = new byte[1024*128];
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
class ConsolePrinter {
    public ConsolePrinter(ConsoleDownloader downloader) {
        this.downloader = downloader;
    }

    private ConsoleDownloader downloader;

    public void startPrint() {
    }
}