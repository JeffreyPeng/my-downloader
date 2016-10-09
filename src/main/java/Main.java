import java.io.File;

/**
 * Created by pen-tpc on 2016/10/8.
 */
public class Main {
    public static final String filePath = "D:/Thunder9.0.16.408.exe";
    public static void main(String[] args) {
        double f = 123456789012345678d;
        System.out.println(f);
        File file = new File(filePath);
        System.out.println(file.length());
    }
}
