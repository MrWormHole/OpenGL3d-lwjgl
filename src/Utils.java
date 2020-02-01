import java.io.*;

public class Utils {
    public static String loadResource(String filePath) throws IOException {
        File file = new File(filePath);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
            stringBuilder.append(line).append("\n");
        }

        return stringBuilder.toString();
    }
}
