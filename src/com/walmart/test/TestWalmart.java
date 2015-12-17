package com.walmart.test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.*;

import com.walmart.main.Cart;
import com.walmart.main.DataProviderSource;
import com.walmart.main.User;
import com.walmart.util.Util;

public class TestWalmart {

	private WebDriver driver;
	private String baseURL;

	// wait time in secs
	private static final int WAIT_TIME = 4;
	private User user = new User();
	private Cart cart = new Cart();

	@BeforeClass
	public void beforeClass() {
		baseURL = "http://www.walmart.com/";
		//
		System.setProperty("webdriver.chrome.driver", "./lib/chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(baseURL);
	}

	// login with existing account
	@Test
	public void login() {
		// click on sign in link
		WebElement signInLink = driver.findElement(By.linkText("Sign In"));
		signInLink.click();
		// wait if elements are not available
		WebElement username = Util.findElement(By.id("login-username"),
				WAIT_TIME, driver);
		WebElement password = Util.findElement(By.id("login-password"),
				WAIT_TIME, driver);
		WebElement loginButton = driver.findElement(By
				.xpath("//button[@data-automation-id='login-sign-in']"));
		username.sendKeys(user.getUsername());
		password.sendKeys(user.getPassword());
		loginButton.submit();
		// check if user is logged in successfully
		driver.manage().timeouts().implicitlyWait(WAIT_TIME, TimeUnit.SECONDS);
		WebElement loggedIn = driver.findElement(By
				.xpath("//span[@class='js-account-logged-in active']"));
		Assert.assertNotNull(loggedIn.getText(), "message on failure");
	}

	// search list of items
	@Test(dependsOnMethods = { "login" }, dataProvider = "searchItemsDataProvider", dataProviderClass = DataProviderSource.class)
	public void testSearch(String item) throws InterruptedException {
		WebElement searchElement = Util.findElement(By.id("search"), WAIT_TIME,
				driver);
		searchElement.clear();
		searchElement.sendKeys(item);
		driver.findElement(
				By.xpath("//button[@class='searchbar-submit js-searchbar-submit']"))
				.submit();
		// FIXME
		Thread.sleep(1000);
		WebElement menuElement = driver
				.findElement(
						By.xpath("//form[@class='js-searchbar searchbar']"))
				.findElement(
						By.xpath("//button[@class='js-flyout-toggle dropdown']"));
		/*
		 * check if main drodown of search menu changes to particular category
		 * instead of
		 * "All depatments' or "All". if it was changed, then select "All
		 * Department" for next search
		 */

		if (menuElement.getText().toLowerCase().equals(item)) {
			Actions action = new Actions(driver);
			action.moveToElement(menuElement).build().perform();
			menuElement.click();
			// find "All Department" from dropdown menu and select it
			Util.findElement(By.xpath("//ul//li/button[@data-cat-id='0']"),
					WAIT_TIME, driver).click();
		} else {
			/*
			 * test whether the number of results available or not.. Text on top
			 * of left bar.. e.g. text "Showing 12 of 34 results "
			 */
			Assert.assertNotNull(
					Util.findElement(By.className("result-summary-container"),
							WAIT_TIME, driver).getText(),
					"test whether result container available");
		}
	}

	/*
	 * after search, test if there is any item in result test can be added to
	 * the cart
	 */
	@Parameters("searchItemName")
	@Test(dependsOnMethods = { "testSearch" })
	public void testIdentifyAnAddableItemToCart(String searchItemName) {

		WebElement searchElement = Util.findElement(By.id("search"), WAIT_TIME,
				driver);
		searchElement.clear();
		searchElement.sendKeys(searchItemName);
		driver.findElement(
				By.xpath("//button[@class='searchbar-submit js-searchbar-submit']"))
				.submit();
		driver.manage().timeouts().pageLoadTimeout(WAIT_TIME, TimeUnit.SECONDS);

		// check if an item can be added to the cart. .
		Assert.assertTrue(Util.isElementAvailable(
				By.xpath("//div[@data-item-id or @data-product-id]"), driver));
	}

	// this method will just add the item in the cart
	@Test(dependsOnMethods = { "testIdentifyAnAddableItemToCart" })
	public void testAddItemToCart() throws InterruptedException {

		/*
		 * Fetching the first item/product from the search result.
		 * 
		 * @data-item-id is unique id of an item.
		 * 
		 * @data-product-id is unique id of a featured products. It shown when
		 * we search for a main category for ed: toys
		 */
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		// FIXME
		Thread.sleep(1000);
		WebElement itemToAddInCart = Util.findElement(By.id("search"),
				WAIT_TIME, driver);
		if (Util.isElementAvailable(By.xpath("//div[@id='tile-container']"),
				driver)) {
			itemToAddInCart = itemToAddInCart.findElement(By
					.xpath("//div[@id='tile-container']//div[@data-item-id]"));
			cart.addItem(itemToAddInCart.getAttribute("data-item-id"));
			Util.findElement(
					By.xpath("//div[@id='tile-container']//div[@data-item-id]"),
					WAIT_TIME, driver)
					.findElement(By.xpath("//a[@class='js-product-image']"))
					.click();

		} else {
			// a product should be available in featured products. It shown when
			// we search for a main category for eg: toys
			itemToAddInCart = driver
					.findElement(By
							.xpath("//div[@id='sponsored-container-middle-2']//div[@data-product-id]"));
			cart.addItem(itemToAddInCart.getAttribute("data-product-id"));
			Util.findElement(
					By.xpath("//div[@id='sponsored-container-middle-2']//div[@data-product-id]"),
					WAIT_TIME, driver)
					.findElement(By.xpath("//a[@class='js-product-image']"))
					.click();
		}
		/*
		 * button with text "add item to cart".. find button by using it's id
		 */
		WebElement addToCartButton = Util.findElement(
				By.id("WMItemAddToCartBtn"), WAIT_TIME, driver);
		// after clicking on this button item will be added to cart
		addToCartButton.click();
	}

	/*
	 * View Cart: test item added is in the cart and also test added item is the
	 * only item in the cart
	 */
	@Test(dependsOnMethods = { "testAddItemToCart" })
	public void testViewCartAddedItem() {
		WebElement viewCartButton = Util.findElement(By.id("PACViewCartBtn"),
				WAIT_TIME, driver);
		viewCartButton.click();
		driver.manage().timeouts().pageLoadTimeout(WAIT_TIME, TimeUnit.SECONDS);

		// view cart. check the cart item is same as the added item
		WebElement addeditem = driver
				.findElement(By
						.xpath("//div[@class='cart-list cart-list-active']//div[@class='cart-item-image']//a[@data-us-item-id]"));
		String itemId = addeditem.getAttribute("data-us-item-id");
		Assert.assertEquals(itemId, cart.getAddedItem(),
				"Check if item in cart is correcr, which we just added");
	}

	// test the number of items in the cart
	@Test(dependsOnMethods = { "testViewCartAddedItem" })
	public void testNumberOfItemsInCart() {

		// find the outer div which contain div for each added item
		WebElement itemsGrid = driver.findElement(By
				.xpath("//div[@data-collection-element='true']"));
		List<WebElement> cartItems = itemsGrid.findElements(By
				.className("cart-item-row"));
		Assert.assertEquals(cartItems.size(), 1,
				"the total items in card should be one");
	}

	// signout and close the driver
	@AfterClass
	public void afterClass() {
		WebElement myAccount = driver.findElement(By.linkText("My Account"));
		Actions action = new Actions(driver);
		action.moveToElement(myAccount).build().perform();
		Util.findElement(By.linkText("Sign Out"), WAIT_TIME, driver).click();
		driver.close();
	}

}
