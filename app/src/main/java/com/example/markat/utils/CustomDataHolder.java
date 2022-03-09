package com.example.markat.utils;

import android.content.Context;

import com.example.markat.models.BusinessMap;
import com.example.markat.models.CustomCategory;
import com.example.markat.models.CustomTag;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDataHolder {

    public static class DataHolderObject {

        Context context;
        private static Map<String, byte[]> mapBusinessToIcon = new HashMap<String, byte[]>();
        private static Map<CustomCategory, List<CustomTag>> mapTagsToCategory = new HashMap<CustomCategory, List<CustomTag>>();
        private static List<CustomCategory> categories = new ArrayList<CustomCategory>();
        private static List<BusinessMap> localBusinesses = new ArrayList<>(); 
        private static List<CustomTag> tags = new ArrayList<>();
        
        public DataHolderObject(Context context) {
            this.context = context;
        }

        public static void initMapBusinessToIcon(List<BusinessMap>businesses) {
            for(int i = 0; i < businesses.size(); i++) {
                mapBusinessToIcon.put(businesses.get(i).getOfficial(), businesses.get(i).getLogo());
            }
        }

        public static byte[] getIconByName(String businessName) {
            return mapBusinessToIcon.get(businessName);
        }
        
        public static void setLocalBusinesses(List<BusinessMap> businesses) {
          localBusinesses = businesses;
          
          if(businesses.get(0).getLogo() != null) {
            for (int i = 0; i < businesses.size(); i++) {
              mapBusinessToIcon.put(businesses.get(i).getOfficial(), businesses.get(i).getLogo());
            }
          }
        }
        
        public static List<BusinessMap> getLocalBusinesses() {
          if (localBusinesses.isEmpty()) {
            return new ArrayList<BusinessMap>();
          } else {
            return new ArrayList<BusinessMap>(localBusinesses);
          }
        }
        public static void initCategories(List<CustomCategory> initialCategories) {
          categories = initialCategories;
        }
        
        public static List<CustomCategory> getCategories() {
          if (categories.isEmpty()) {
            return new ArrayList<CustomCategory>();

          } else {
            return new ArrayList<CustomCategory>(categories);
          }
        }
        
        public static void initTags(List<CustomTag> initialTags) {
          tags = initialTags;
          mapTagsToCategory();
        }
        
        public static void mapTagsToCategory() {
          
            for(CustomCategory category : categories) {
              List<CustomTag> tempTags = new ArrayList<>();
              
              for(CustomTag tag : tags) {
                if(category.getId().equals(tag.getParentCategory())) {
                  tempTags.add(tag);
                }
              }
              
              mapTagsToCategory.put(category, tempTags);
            }
        }
        
        public static Map<CustomCategory, List<CustomTag>> getMapTagsToCategory() {
          return mapTagsToCategory;
        }
    }
}
