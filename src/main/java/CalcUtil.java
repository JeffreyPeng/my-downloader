/**
 * Created by pengyuxiang1 on 2016/11/10.
 */
public class CalcUtil {

    public static String ByteToShow(long bytes) {
        if (bytes < 1000L) {
            return bytes + "B";
        } else if (bytes < 1000_000L) {
            return String.format("%.2fKB", bytes / 1000.0);
        } else if (bytes < 1000_000_000L) {
            return String.format("%.2fMB", bytes / 1000_000.0);
        } else {
            return String.format("%.2fGB", bytes / 1000_000_000.0);
        }
    }
    public static String SecondToShow(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 60 * 60) {
            if (seconds % 60 == 0) {
                return (seconds / 60) + "m";
            } else {
                return (seconds / 60) + "m" + (seconds % 60) + "s";
            }
        } else if (seconds < 24 * 60 * 60) {
            if ((seconds / 60 % 60) == 0) {
                return (seconds / 60 / 60) + "h";
            } else {
                return (seconds / 60 / 60) + "h" + (seconds / 60 % 60) + "m";
            }
        } else {
            if ((seconds / 60 / 60 % 24) == 0) {
                return (seconds / 60 / 60 / 24) + "d";
            } else {
                return (seconds / 60 / 60 / 24) + "d" + (seconds / 60 / 60 % 24) + "h";
            }
        }
    }
    public static String ParseFileName(String path) {
        return  path.substring(path.lastIndexOf('/') + 1, path.length());
    }
}
