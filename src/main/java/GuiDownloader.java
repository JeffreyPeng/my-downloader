import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

/**
 * Created by pengyuxiang1 on 2016/11/10.
 */
public class GuiDownloader {

    JFrame frame;
    JTextField text1;
    JTextField text2;
    JLabel stateLabel;
    JProgressBar progressbar;

    public GuiDownloader() {
        frame = new JFrame("Gui Downloader");
        frame.setSize(400, 230);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((dim.width - frame.getWidth()) / 2, (dim.height - frame.getHeight()) / 2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);

        JLabel label1 = new JLabel("下载地址：", JLabel.CENTER);
        label1.setBounds(20, 20, 60, 22);
        text1 = new JTextField();
        text1.setBounds(90, 20, 280, 20);
        JLabel label2 = new JLabel("本地路径：", JLabel.CENTER);
        label2.setBounds(20, 60, 60, 22);
        text2 = new JTextField("C:/");
        text2.setBounds(90, 60, 280, 20);
        JButton button = new JButton("开始下载");
        button.setBounds(160, 100, 80, 22);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = text1.getText();
                String filePath = text2.getText();
                new Thread(new Downloader(url, filePath, progressbar, stateLabel)).start();
            }
        });
        progressbar = new JProgressBar();
        progressbar.setOrientation(JProgressBar.HORIZONTAL);
        progressbar.setMinimum(0);
        progressbar.setMaximum(100);
        progressbar.setValue(0);
        progressbar.setStringPainted(true);
        progressbar.setBorderPainted(true);
        progressbar.setBackground(Color.pink);
        progressbar.setBounds(20, 140, 360, 20);
        stateLabel = new JLabel("未开始", JLabel.LEFT);
        stateLabel.setBounds(20, 170, 360, 20);
        frame.add(label1);
        frame.add(label2);
        frame.add(text1);
        frame.add(text2);
        frame.add(button);
        frame.add(progressbar);
        frame.add(stateLabel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
            setDefaultSize(12);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new GuiDownloader();
    }

    public static void setDefaultSize(int size) {
        Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
        Object[] keys = keySet.toArray(new Object[keySet.size()]);
        for (Object key : keys) {
            if (key != null && key.toString().toLowerCase().contains("font")) {
                System.out.println(key);
                Font font = UIManager.getDefaults().getFont(key);
                if (font != null) {
                    font = font.deriveFont((float)size);
                    UIManager.put(key, font);
                }
            }
        }
    }
}
class Downloader implements Runnable {
    private String url;
    private String filePath;
    JLabel stateLabel;
    JProgressBar progressbar;
    private long pos;
    private long length;

    public long getPos() {
        return pos;
    }
    public long getLength() {
        return length;
    }

    public Downloader (String url, String filePath, JProgressBar progressbar, JLabel stateLabel) {
        this.url = url;
        this.filePath = filePath;
        this.stateLabel = stateLabel;
        this.progressbar = progressbar;
    }
    public void updateView(int progress, String stateString) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressbar.setValue(progress);
                stateLabel.setText(stateString);
            }
        });
    }
    @Override
    public void run() {
        try {
            URL downloadUrl = new URL(url);
            URLConnection urlConnection = downloadUrl.openConnection();
            urlConnection.connect();
            length = urlConnection.getContentLengthLong();
            InputStream inputStream = urlConnection.getInputStream();
            byte []bytes = new byte[1024*8];
            int readCount = inputStream.read(bytes);
            String finalPath = filePath + "/" + CalcUtil.ParseFileName(urlConnection.getURL().toString());
            FileOutputStream outputStream = new FileOutputStream(finalPath);
            pos = 0;
            new GuiPrinter(this).startPrint();
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
class GuiPrinter implements Runnable {
    public GuiPrinter(Downloader downloader) {
        this.downloader = downloader;
    }

    private Downloader downloader;

    public void startPrint() {
        startTime = System.currentTimeMillis();
        new Thread(this).start();
    }
    long lastSpeed = 0;
    long lastPos = 0;
    long startTime = 0;

    @Override
    public void run() {
        try {
            while (true) {
                long pos = downloader.getPos();
                long length = downloader.getLength();
                int percent = (int)(pos * 100 /length);
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
                if (percent == 100) {
                    downloader.updateView(percent, String.format("下载完成: % 4d%%%8s%11s%11s", percent, downloadedSize, speed, timsShow));
                    break;
                } else {
                    downloader.updateView(percent, String.format("下载中: % 4d%%%8s%11s%11s", percent, downloadedSize, speed, timsShow));
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
