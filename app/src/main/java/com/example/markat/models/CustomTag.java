package com.example.markat.models;

public class CustomTag {
  
  private String id;
  private String title;
  private String description;
  private byte[] logo;
  private String logoAsBase64;
  private String parentCategory;
  
  public CustomTag(String id, String title, String description, byte[] logo, String parentCategory) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.logo = logo;
    this.parentCategory = parentCategory;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setLogo(byte[] logo) {
    this.logo = logo;
  }

  public byte[] getLogo() {
    return logo;
  }

  public void setParentCategory(String parentCategory) {
    this.parentCategory = parentCategory;
  }

  public String getParentCategory() {
    return parentCategory;
  }
}
