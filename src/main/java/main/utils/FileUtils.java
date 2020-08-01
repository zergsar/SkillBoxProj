package main.utils;

import java.io.File;

public class FileUtils {

    public static boolean isFileSizeLessThan5Mb(String str)
    {
        boolean result = false;
        File file = new File(str);

        if(file.exists())
        {
            double fileSize = (double) file.length()/(1024*1024);
            result = fileSize <= 5;
        }

        return result;
    }


}
