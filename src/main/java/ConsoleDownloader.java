import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by pen-tpc on 2016/10/8.
 */
public class ConsoleDownloader {

    public static final String url = "http://down.sandai.net/thunder9/Thunder9.0.16.408.exe";
    public static final String filePath = "D:/Thunder9.0.16.408.exe";

    public static void main(String[] args) {
        try {
            URL downloadUrl = new URL(url);
            URLConnection urlConnection = downloadUrl.openConnection();
            urlConnection.connect();
            long contentLength = urlConnection.getContentLengthLong();
            InputStream inputStream = urlConnection.getInputStream();
            byte []bytes = new byte[1024*128];
            int readCount = inputStream.read(bytes);
            FileOutputStream outputStream = new FileOutputStream(filePath);
            long length = 0;
            while (readCount != -1) {
                outputStream.write(bytes, 0, readCount);
                length += readCount;
                System.out.println(length * 100 / contentLength  + "%");
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
