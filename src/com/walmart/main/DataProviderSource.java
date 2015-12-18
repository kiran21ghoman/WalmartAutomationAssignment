package com.walmart.main;

import org.testng.annotations.DataProvider;

public class DataProviderSource {
	
	@DataProvider(name = "searchItemsDataProvider")
	public static Object[][] createData() {
		return new Object[][] { { "tv" }, { "Socks" },{ "dvd" }, { "toys" },{ "iphone" }, };
	}
	
}
