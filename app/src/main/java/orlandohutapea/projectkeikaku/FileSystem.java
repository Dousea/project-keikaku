package orlandohutapea.projectkeikaku;

import java.io.IOException;
import java.io.InputStream;

public class FileSystem {
    public static String readString(InputStream stream) throws IOException {
        String string = "";
        int val = stream.read();

        while (val > 0) {
            string = string.concat(String.valueOf((char)val));
            val = stream.read();
        }

        return string;
    }

    public static long read(InputStream stream, int bytes, boolean signed) throws IOException {
        long val = 0;
        char chars[] = new char[bytes];

        for (int i = 0; i < bytes; i++)
            if ((chars[i] = (char)stream.read()) == -1)
                throw new IOException("stream has reached EOF before completing the read");

        for (int i = 0; i < chars.length; i++)
            val += chars[i] * 2 ^ (i * 8);

        if (signed) {
            int bits = bytes * 8;
            val = val > (2 ^ (bits - 1) - 1) ? (val - 2 ^ bits) : val;
        }

        return val;
    }
}
