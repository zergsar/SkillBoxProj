package main.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

  public static boolean isFileSizeLessThan5Mb(String str) {
    boolean result = false;
    File file = new File(str);

    if (file.exists()) {
      double fileSize = (double) file.length() / (1024 * 1024);
      result = fileSize <= 5;
    }

    return result;
  }

  public static String uploadFileToSubDir(String subDirNames, MultipartFile image) {
    File subdir = new File(subDirNames);
    if (!subdir.exists() || !subdir.isDirectory()) {
      subdir.mkdirs();
    }
    File subdirWithFileName = new File(subdir.getPath() + "/" + image.getOriginalFilename());
    try {
      byte[] bytes = image.getBytes();
      BufferedOutputStream stream =
          new BufferedOutputStream(new FileOutputStream(subdirWithFileName));
      stream.write(bytes);
      stream.close();
      subdirWithFileName.getPath();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return subdirWithFileName.getAbsolutePath();
  }


}
