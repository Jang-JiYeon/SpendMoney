package com.kakaopay.sm.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.kakaopay.sm.common.SpendMoneyApiResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SpendMoneyControllerTests {
	private static Logger logger = LogManager.getLogger(SpendMoneyControllerTests.class);
	
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired 
    private WebApplicationContext ctx;
    	
    
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
	public void send_Test() throws Exception {
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
	public void empty_Header_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        Map<String, String> map = new HashMap<>();
        map.put("sendPsersonNum", "3");
        map.put("sendTotalMoney", "30000");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<SpendMoneyApiResponse> response 
        		= restTemplate.postForEntity("/api/v1/sendMoney", request, SpendMoneyApiResponse.class);
        logger.info("sendMoney API: " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("0001");
    }
	
	@Test
	public void check_Header_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("X-USER-ID", "12345678910123456789dfajflk");
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
        assertThat(response.getBody().getCode()).isEqualTo("0002");
    }
	
	@Test
	public void empty_Send_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("X-USER-ID", "12345678910123456789101234567891");
        headers.set("X-ROOM-ID", "qwertyuiopasdfghjklzxcvbnmqwer");

        Map<String, String> map = new HashMap<>();	//인원,금액 비움

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<SpendMoneyApiResponse> response 
        		= restTemplate.postForEntity("/api/v1/sendMoney", request, SpendMoneyApiResponse.class);
        logger.info("sendMoney API: " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("0003");
    }
	
	@Test
	public void empty_Token_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("X-USER-ID", "12345678910123456789101234567891");
        headers.set("X-ROOM-ID", "qwertyuiopasdfghjklzxcvbnmqwer");

        Map<String, String> map = new HashMap<>();	//token 비움

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<SpendMoneyApiResponse> response 
        		= restTemplate.postForEntity("/api/v1/receiveMoney", request, SpendMoneyApiResponse.class);
        logger.info("receiveMoney API: " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("0005");
    }
	
	@Test
	public void noSearch_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("X-USER-ID", "12345678910123456789101234567891");
        headers.set("X-ROOM-ID", "qwertyuiopasdfghjklzxcvbnmqwer");

        Map<String, String> map = new HashMap<>();
        map.put("token", "XXX");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<SpendMoneyApiResponse> response 
        		= restTemplate.postForEntity("/api/v1/searchMoney", request, SpendMoneyApiResponse.class);
        logger.info("searchMoney API: " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("0006");
    }	
	
	@Test
	public void timeover_Search_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("X-USER-ID", "12345678910123456789101234567891");
        headers.set("X-ROOM-ID", "qwertyuiopasdfghjklzxcvbnmqwer");

        Map<String, String> map = new HashMap<>();
        map.put("token", "klw");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<SpendMoneyApiResponse> response 
        		= restTemplate.postForEntity("/api/v1/searchMoney", request, SpendMoneyApiResponse.class);
        logger.info("searchMoney API: " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("0007");
    }
	
	@Test
	public void receive_Test() throws Exception {
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
        assertThat(response.getBody().getCode()).isEqualTo("0008");
    }
	
	@Test
	public void noReceive_Test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("X-USER-ID", "12345678910123456789101234567891");
        headers.set("X-ROOM-ID", "qwertyuiopasdfghjklzxcvbnmqwer");

        Map<String, String> map = new HashMap<>();
        map.put("token", "XXX");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<SpendMoneyApiResponse> response 
        		= restTemplate.postForEntity("/api/v1/receiveMoney", request, SpendMoneyApiResponse.class);
        logger.info("receiveMoney API: " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("0010");
    }
	
	@Test
	public void search_Test() throws Exception {
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
        assertThat(response.getBody().getCode()).isEqualTo("0000");
    }
    
}
