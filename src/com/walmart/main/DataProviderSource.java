package com.walmart.main;

import org.testng.annotations.DataProvider;

public class DataProviderSource {
	
	@DataProvider(name = "searchItemsDataProvider")
	public static Object[][] createData() {
		// "tv", "socks", "dvd", "toys", "iPhone"
		return new Object[][] { { "tv" }, { "Socks" }, };
	}
	
}
