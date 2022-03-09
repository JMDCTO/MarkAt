package com.example.markat.models;

public class CustomCategory {
  
  String id;
  String title;
  String description;

  byte[] logo;
  String logoAsBase64;
  
  public CustomCategory(String id, String title, String description, byte[] logo) {
      this.id = id;
      this.title = title;
      this.description = description;
      this.logo = logo;
  }

  public String getId() {
    return id;
  }

  public byte[] getLogo() {
    return logo;
  }

  public String getDescription() {
    return description;
  }

  public String getTitle() {
    return title;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setLogo(byte[] logo) {
    this.logo = logo;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
