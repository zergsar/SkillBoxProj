package main.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

  private static final int BYTE_IN_MEGABYTE = 1000000;

  public static boolean isValidImageFileFormat(MultipartFile mpf) {
    String contentType = mpf.getContentType();
    String jpgType = "image/jpeg";
    String pngType = "image/png";
    return Objects.requireNonNull(contentType).equals(jpgType) || contentType.equals(pngType);
  }

  public static boolean isValidMpfFileSize(MultipartFile mpf, int maxSizeMb) {
    boolean result = true;
    if (mpf.getSize() / BYTE_IN_MEGABYTE > maxSizeMb) {
      result = false;
    }
    return result;
  }

  public static boolean isMpfFileNotNull(MultipartFile mpf) {
    boolean result = true;
    if (mpf.isEmpty() || mpf.getSize() == 0) {
      result = false;
    }
    return result;
  }

  public static String uploadFile(String defaultUploadDir, int lenChar, int countSubDir, MultipartFile image){
    String subDirNames = Generator.getRandomPathToImage(lenChar, countSubDir);
    String dir =
        (defaultUploadDir.endsWith("/") ? defaultUploadDir : defaultUploadDir + "/") + subDirNames;

    String pathToRes = getUploadFileToSubDir(dir, image).replace("\\", "/")
        .replace(defaultUploadDir, "");
    pathToRes = pathToRes.startsWith("/") ? "/upload" + pathToRes : "/upload/" + pathToRes;
    return pathToRes;
  }

  private static String getUploadFileToSubDir(String subDirNames, MultipartFile image) {
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
    } catch (IOException e) {
      e.printStackTrace();
    }

    return subdirWithFileName.getPath();
  }


}
