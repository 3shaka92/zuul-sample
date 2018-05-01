package sample.zuul;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"zuul.routes.sample-with-regex.url=http://localhost:8989", "zuul.routes.sample-with-regex.path=/some_*_*/**"})
@AutoConfigureMockMvc
public class StripRegexPrefixPassthroughTest {

    @Autowired
    private MockMvc mockMvc;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8989);

    @Test
    public void testPassthroughCall() throws Exception {

        stubFor(WireMock.post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": \"1\",\n" +
                                "  \"received\": \"sample payload\",\n" +
                                "  \"ack\": \"ack\"\n" +
                                "}\n")));

        mockMvc.perform(
                post("/some_a_b/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"id\": \"1\",\n" +
                                "  \"payload\": \"sample payload\",\n" +
                                "  \"delay\": 0\n" +
                                "}\n"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json("{\n" +
                        "  \"id\": \"1\",\n" +
                        "  \"received\": \"sample payload\",\n" +
                        "  \"ack\": \"ack\"\n" +
                        "}"));

        verify(
                postRequestedFor(urlMatching("/messages"))
                        .withRequestBody(matching(".*sample.*"))
        );
    }
}
