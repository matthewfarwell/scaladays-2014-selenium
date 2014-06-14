package uk.co.farwell.scaladays.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AmIFamous {
    public static void main(String[] args) {
        final WebDriver driver = new FirefoxDriver();

        try {
            Wait<WebDriver> wait = new WebDriverWait(driver, 30);

            // open the URL
            driver.get("http://www.google.ch/");

            // search query
            driver.findElement(By.name("q")).sendKeys("Matthew Farwell");

            // submit the search
            driver.findElement(By.name("btnG")).click();

            // loop until search completes
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver webDriver) {
                    System.out.println("Trying...");
                    return webDriver.findElement(By.id("resultStats")) != null;
                }
            });

            // do we have the correct result?
            boolean found = driver.findElement(By.tagName("body")).getText().contains("farwell.co.uk");
            System.out.println((found ? "You are famous" : "Not famous enough, sorry"));

        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }
}