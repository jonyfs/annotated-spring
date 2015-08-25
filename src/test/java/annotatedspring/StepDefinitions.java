package annotatedspring;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.fluentlenium.adapter.FluentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;

import static org.fluentlenium.core.filter.FilterConstructor.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@ContextConfiguration(classes = Application.class, loader = SpringApplicationContextLoader.class)
@WebIntegrationTest({"server.port=0", "management.port=0"})
public class StepDefinitions  extends FluentTest {
    private WebDriver driver;

    @Value("${local.server.port}")
    protected int port;

    private String baseUrl;

    @Before
    public void before() {
        setupFluentlenium();
    }

    @After
    public void after(){
        teardownFluentlenium();
    }

    @Override
    public WebDriver getDefaultDriver() {
        if (driver == null) {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            driver = new PhantomJSDriver(capabilities);
        }

        return driver;
    }

    protected void setupFluentlenium() {
        try {
            baseUrl = new URI("http://localhost:" + port + "/").normalize().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.initFluent(getDefaultDriver());
        this.initTest();
    }

    protected void teardownFluentlenium() {
        this.quit();
    }

    @Given("^there are no episodes$")
    public void there_are_no_episodes() throws Throwable {
        // Nothing
    }

    @When("^I create an episode$")
    public void I_create_an_episode() throws Throwable {
        goTo(baseUrl);
        click("#create-episode");
        fill("#title").with("Episode Title");
        fill("#summary").with("Episode Summary");
        fill("#notes").with("Episode Notes");
        fill("#youtubeId").with("YouTube ID");
        fill("#duration").with("42");
        fill("#sourcecodeUrl").with("https://github.com/AnnotatedSpring/episode-001");
        submit("#create");
    }

    @Then("^I should see it on the episodes page$")
    public void I_should_see_it_on_the_episodes_page() throws Throwable {
        assertThat(findFirst("tbody tr").getText(), containsString("Episode Title"));
    }

    @And("^I should be able to view its details$")
    public void I_should_be_able_to_view_its_details() throws Throwable {
        click("#episode1");
        assertThat(findFirst("h1").getText(), containsString("Episode Title"));
    }

    @Given("^there is an existing episode$")
    public void there_is_an_existing_episode() throws Throwable {
        I_create_an_episode();
    }

    @When("^I edit that episode$")
    public void I_edit_that_episode() throws Throwable {
        findFirst("a", withText("Episode Title")).click();
        findFirst("a", withText("Edit")).click();

        assertThat(find("#title").getValue(), containsString("Episode Title"));

        fill("#title").with("Edited Episode Title");
        fill("#summary").with("Edited Episode Summary");
        fill("#notes").with("Edited Episode Notes");
        fill("#youtubeId").with("Edited YouTube ID");
        fill("#duration").with("43");
        fill("#sourcecodeUrl").with("https://github.com/AnnotatedSpring/episode-001b");

        submit("#update");
    }

    @Then("^I should see my changes reflect on the episode page$")
    public void I_should_see_my_changes_reflect_on_the_episode_page() throws Throwable {
        assertThat(findFirst("tbody tr").getText(), containsString("Edited Episode Title"));

    }

    @And("^I should see my changes reflected when I view its details$")
    public void I_should_see_my_changes_reflected_when_I_view_its_details() throws Throwable {
        click("#episode1");
        assertThat(findFirst("h1").getText(), containsString("Edited Episode Title"));
    }
}