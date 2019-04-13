package recommendation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import db.mysql.MySQLConnection;


import entity.Item;
public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon){
		List<Item> recommendedItems = new ArrayList<>();
		DBConnection connection = DBConnectionFactory.getConnection();
		
		// get all favorite items
		Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
		
		
		//get all categories and sort them by count number
		Map<String, Integer> allCategories = new HashMap<>();
		for (String itemId :favoritedItemIds) {
			Set<String> categories = connection.getCategories(itemId);
			for (String category : categories) {
				allCategories.put(category, allCategories.getOrDefault(category, 0) + 1);
			}
		}
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
			return Integer.compare(e2.getValue(), e1.getValue());
		});
		// search based on category, filter out favorite items
		
		Set<String> visitedItemIds = new HashSet<>();
		for (Entry<String, Integer> category : categoryList) {
			List<Item> items = connection.searchItems(lat, lon, category.getKey());
			for (Item item : items) {
				if (!favoritedItemIds.contains(item) && !visitedItemIds.contains(item)) {
					recommendedItems.add(item);
					visitedItemIds.add(userId);
					
				} 
			}
			
		}
		connection.close();
		return recommendedItems;

		
	}

}
