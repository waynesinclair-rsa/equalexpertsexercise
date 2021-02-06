import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*
TODO - Refactoring opportunities
        : Use page model
        : Use a reporting plugin - Serenity
        : Use a build tool - Maven
*/


public class BookingTest {
    private WebDriver driver;
    private String uniqueFirstName1 = "Wayne";
    private String uniqueFirstName2 = "Wayne";
    private By FirstNameLocator = By.id("firstname");
    private By SurNameLocator = By.id("lastname");
    private By TotalPriceLocator = By.id("totalprice");
    private By DepositPaidLocator = By.id("depositpaid");
    private By CheckInLocator = By.id("checkin");
    private By CheckOutLocator = By.id("checkout");
    private By SaveButtonLocator = By.xpath("//input[@value=' Save ']");

    public BookingTest() {
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();

        while(count-- != 0) {
            int character = (int)(Math.random() * (double)"ABCDEFGHIJKLMNOPQRSTUVWXYZ".length());
            builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(character));
        }

        return builder.toString();
    }

    private String checkInDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now().plusDays(2L);
        return dtf.format(now);
    }

    private String checkOutDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now().plusDays(5L);
        return dtf.format(now);
    }

    public void add_a_new_hotel_booking(String fn, String sn, String tp, String dp, String cid, String cod) {
        this.driver.findElement(this.FirstNameLocator).sendKeys(fn);
        this.driver.findElement(this.SurNameLocator).sendKeys(sn);
        this.driver.findElement(this.TotalPriceLocator).sendKeys(tp);
        WebElement DepositPaid = this.driver.findElement(this.DepositPaidLocator);
        Select DepositDropDownSelect = new Select(DepositPaid);
        DepositDropDownSelect.selectByVisibleText(dp);
        this.driver.findElement(this.CheckInLocator).sendKeys(cid);
        this.driver.findElement(this.CheckOutLocator).sendKeys(cod);
        this.driver.findElement(this.SaveButtonLocator).click();
    }

    // The firstname will be unique, so check if it exists on the screen
    // TODO - Refactoring opportunity - check if rest of input fields are correct
    private Boolean NewBookingAdded(String fn) {
        try {
            WebDriverWait w = new WebDriverWait(this.driver, 60L);
            w.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), fn));
            return true;
        } catch (TimeoutException var3) {
            return false;
        }
    }

    public void should_see_booking_added_to_list(String fn) {
        MatcherAssert.assertThat(this.inputLineVisible(), CoreMatchers.is(true));
        Assert.assertEquals(this.NewBookingAdded(fn), true);
    }

    /*
    There is often a pause after the user has captured a new booking and clicked save
    before the screen is refreshed amd the system presenting the input fields for the next booking
    So to check if the screen has been refreshed, we check to see if the input line is visible

     */
    public Boolean inputLineVisible() {
        try {
            WebDriverWait w = new WebDriverWait(this.driver, 60L);
            w.until(ExpectedConditions.presenceOfElementLocated(this.FirstNameLocator));
        } catch (TimeoutException var2) {
            return false;
        }

        return true;
    }

    private void delete_hotel_booking(String fn) {
        List<WebElement> allBookings = this.driver.findElements(By.xpath("//*[@id='bookings']/div[@class='row']"));
        int allBookingsCount = allBookings.size();

        for(int i = allBookingsCount - 1; i >= 1; --i) {
            List<WebElement> columns = allBookings.get(i).findElements(By.xpath("div/p"));
            List<WebElement> deleteButtons = allBookings.get(i).findElements(By.xpath("div/input"));
            String currentFirstName = columns.get(0).getText();
            if (currentFirstName.equals(fn))
                (deleteButtons.get(0)).click();
                break;
            }
        }

    private Boolean bookingHasBeenDeleted(String fn) {
        try {
            WebDriverWait w = new WebDriverWait(this.driver, 60L);
            w.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), fn)));
            return true;
        } catch (TimeoutException var3) {
            return false;
        }
    }

    @Before
    public void SetUp() {
        System.setProperty("webdriver.chrome.driver", ".\\test\\chromedriver.exe");
        this.driver = new ChromeDriver();
        String baseUrl = "http://hotel-test.equalexperts.io/";
        this.driver.get(baseUrl);
        String expectedTitle = "Hotel booking form";
        String actualTitle = this.driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle);
    }

    @Test
    public void Adding_A_New_Booking_Should_See_Booking_Displayed_In_List() {
        this.uniqueFirstName1 = this.uniqueFirstName1 + randomAlphaNumeric(10);
        this.add_a_new_hotel_booking(this.uniqueFirstName1, "Sinclair", "50", "false", this.checkInDate(), this.checkOutDate());
        this.should_see_booking_added_to_list(this.uniqueFirstName1);
        System.out.println("Test 1 - Passed: Successfully added user with firstname " + this.uniqueFirstName1);
    }

    @Test
    public void Booking_Should_Not_Be_In_List_After_Deletion() {
        this.uniqueFirstName2 = this.uniqueFirstName2 + randomAlphaNumeric(10);
        this.add_a_new_hotel_booking(this.uniqueFirstName2, "Sinclair", "55", "true", this.checkInDate(), this.checkOutDate());
        this.should_see_booking_added_to_list(this.uniqueFirstName2);
        this.delete_hotel_booking(this.uniqueFirstName2);
        MatcherAssert.assertThat(this.bookingHasBeenDeleted(this.uniqueFirstName2), CoreMatchers.is(true));
        System.out.println("Test 2 - Passed: Successfully added and deleted user with firstname " + this.uniqueFirstName2);
    }

    @After
    public void TearDown() {
        this.driver.quit();
    }
}
