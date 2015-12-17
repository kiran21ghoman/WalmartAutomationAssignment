package com.walmart.util;

import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Util {
	
	public static WebElement findElement(By by, int waitTime, WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		WebElement element = wait.until(ExpectedConditions
				.presenceOfElementLocated(by));
		return element;
	}

	public static boolean isElementAvailable(By by, WebDriver driver) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
}
