import java.io.Console;
import java.io.File;

/**
 * Created by pen-tpc on 2016/10/8.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        if (console != null) {
            console.writer().format("asdf");
            console.writer().write("\b\b");
            console.writer().write("g");
            console.flush();
        } else {
            System.out.println("必须在标准输入、输出流未被重定向的原始控制台中使用，IDE控制台无法使用！");
        }
    }
}
