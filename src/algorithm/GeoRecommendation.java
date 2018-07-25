package algorithm;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

import java.util.*;

public class GeoRecommendation {
    public List<Item> recommendItemIds(String userId, double lat, double lon) {
        List<Item> recommendedItems = new ArrayList<>();

        DBConnection connection = DBConnectionFactory.getConnection();

        // 1. Get all favorite itemIds of the user
        Set<String> favoriteItemIds = connection.getFavoriteItemIds(userId);

        // 2. Get all categories of the favorite items, sort in descending-frequency order
        Map<String, Integer> allCategories = new HashMap<>();
        for (String itemId : favoriteItemIds) {
            Set<String> categories = connection.getCategories(itemId);
            for (String category : categories) {
                allCategories.put(category, allCategories.getOrDefault(category, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
        Collections.sort(categoryList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return Integer.compare(o2.getValue(), o1.getValue());
            }
        });

        // 3. Do search based on category, filter out favorite items, sort by distance
        Set<String> visitedItemIds = new HashSet<>();
        for (Map.Entry<String, Integer> entry : categoryList) {
            List<Item> items = connection.searchItems(lat, lon, entry.getKey());
            List<Item> filteredItems = new ArrayList<>();

            for (Item item : items) {
                if (!visitedItemIds.contains(item.getItemId()) && !favoriteItemIds.contains(item.getItemId())) {
                    filteredItems.add(item);
                    visitedItemIds.add(item.getItemId());
                }
            }

            Collections.sort(filteredItems, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    return Double.compare(o1.getDistance(), o2.getDistance());
                }
            });

            recommendedItems.addAll(filteredItems);
        }

        connection.close();
        return recommendedItems;
    }
}

