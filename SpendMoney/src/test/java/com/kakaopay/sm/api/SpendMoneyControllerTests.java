package com.kakaopay.sm.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.kakaopay.sm.SpendMoneyApplicationTests;
import com.kakaopay.sm.api.receive.ReceiveMoneyController;
import com.kakaopay.sm.api.send.SendMoneyService;
import com.kakaopay.sm.common.SpendMoneyApiResponse;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Transactional
//@AutoConfigureMockMvc
//@WebMvcTest(SendMoneyController.class)
//@Slf4j
//public class SendMoneyControllerTests {
public class SpendMoneyControllerTests extends SpendMoneyApplicationTests {
	private static Logger logger = LogManager.getLogger(ReceiveMoneyController.class);
	
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired 
    private WebApplicationContext ctx;
    	
    //@Autowired
    //private SendMoneyService service;
    
    //@BeforeEach() //Junit4의 @Before
    @Before
    public void setup() { 
    	logger.info("JUnit Test");
    	this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx) 
    			.addFilters(new CharacterEncodingFilter("UTF-8", true)) //필터 추가 
    			//.alwaysDo(print()) 
    			.build();
    }
    
	@Test
    void send_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("X-USER-ID", "12345678910123456789101234567891");
        headers.set("X-ROOM-ID", "qwertyuiopasdfghjklzxcvbnmqwer");

        Map<String, String> map = new HashMap<>();
        map.put("sendPsersonNum", "3");
        map.put("sendTotalMoney", "30000");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<SpendMoneyApiResponse> response 
        		= restTemplate.postForEntity("/api/v1/sendMoney", request, SpendMoneyApiResponse.class);
        logger.info("sendMoney API: " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("0000");
    }
	
	@Test
    void receive_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("X-USER-ID", "12345678910123456789101234567891");
        headers.set("X-ROOM-ID", "qwertyuiopasdfghjklzxcvbnmqwer");

        Map<String, String> map = new HashMap<>();
        map.put("token", "6eh");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<SpendMoneyApiResponse> response 
        		= restTemplate.postForEntity("/api/v1/receiveMoney", request, SpendMoneyApiResponse.class);
        logger.info("receiveMoney API: " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("0010");
    }
	
	@Test
    void search_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("X-USER-ID", "12345678910123456789101234567891");
        headers.set("X-ROOM-ID", "qwertyuiopasdfghjklzxcvbnmqwer");

        Map<String, String> map = new HashMap<>();
        map.put("token", "6eh");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<SpendMoneyApiResponse> response 
        		= restTemplate.postForEntity("/api/v1/searchMoney", request, SpendMoneyApiResponse.class);
        logger.info("searchMoney API: " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("0006");
    }
    
}
