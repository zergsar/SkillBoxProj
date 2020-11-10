package main.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import org.springframework.web.multipart.MultipartFile;

public class MultipartImage implements MultipartFile, Serializable {


  private byte[] bytes;
  String name;
  String originalFilename;
  String contentType;
  boolean isEmpty;
  long size;

  public MultipartImage() {
    this.isEmpty = false;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOriginalFilename(String originalFilename) {
    this.originalFilename = originalFilename;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalFilename() {
    return originalFilename;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return isEmpty;
  }

  @Override
  public long getSize() {
    return size;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return bytes;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void transferTo(File dest) throws IOException, IllegalStateException {
    // TODO Auto-generated method stub
  }


  public static class Builder {

    private MultipartImage newMultipartImage;

    public Builder() {
      newMultipartImage = new MultipartImage();
    }

    public Builder fromBytesArray(byte[] bytes) {
      newMultipartImage.setBytes(bytes);
      return this;
    }

    public Builder withName(String name) {
      newMultipartImage.setName(name);
      return this;
    }

    public Builder withOriginalFilename(String originalFilename) {
      newMultipartImage.setOriginalFilename(originalFilename);
      return this;
    }

    public Builder withContentType(String contentType) {
      newMultipartImage.setContentType(contentType);
      return this;
    }

    public Builder withSize(long size) {
      newMultipartImage.setSize(size);
      return this;
    }

    public Builder withEmpty(boolean isEmpty) {
      newMultipartImage.setEmpty(isEmpty);
      return this;
    }

    public MultipartImage build() {
      return newMultipartImage;
    }

  }

}

