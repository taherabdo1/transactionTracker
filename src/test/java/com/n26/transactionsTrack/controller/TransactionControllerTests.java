package com.n26.transactionsTrack.controller;

import static org.junit.Assert.assertEquals;

import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.awt.PageAttributes.MediaType;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.jayway.restassured.RestAssured;
import com.n26.transactionsTrack.entity.TransactionEntity;
import com.n26.transactionsTrack.service.TransactionsService;
import com.n26.transactionsTrack.utils.Constants;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTests {
	private final static Logger logger = LoggerFactory.getLogger(TransactionControllerTests.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TransactionController transactionController;

	@MockBean
	private TransactionsService transactionsService;

	final private TransactionEntity transactionEntityWithinLastMinute = new TransactionEntity();

	@Before
	public void init() {
		transactionEntityWithinLastMinute.setAmount(23.5);
		transactionEntityWithinLastMinute.setTimestamp(Instant.now().toEpochMilli());
	}

	// @Test
	public void getTest() throws Exception {
		given(transactionController.test()).willReturn("test");
		mockMvc.perform(get("/v1/transactions").contentType(ALL_VALUE)).andExpect(status().isOk());
	}

	@Test
	public void testMakeNewTransactionHappeyScienario() throws Exception {

		Mockito.when(transactionsService.addNewTransaction(Mockito.any(TransactionEntity.class)))
				.thenReturn(Constants.ADD_WITHIN_LAST_MINUTE);
		String request = "{\r\n" + "	\"amount\":13.3,\r\n" + "	\"timestamp\":1529756760467\n" + "}";
		logger.debug(request);
		// Send course as body to v1/transactions
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/transactions")
				.accept(org.springframework.http.MediaType.APPLICATION_JSON).content(request)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse responseBody = result.getResponse();

		assertEquals(HttpStatus.CREATED.value(), responseBody.getStatus());

	}

	@Test
	public void testMakeNewTransactionWithTransactionOlderThanOneMinute() throws Exception {

		Mockito.when(transactionsService.addNewTransaction(Mockito.any(TransactionEntity.class)))
				.thenReturn(Constants.BEFORE_LAST_MINUTE);
		String request = "{\r\n" + "	\"amount\":13.3,\r\n" + "	\"timestamp\":1529756760467\n" + "}				";
		// Send course as body to v1/transactions
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/transactions")
				.accept(org.springframework.http.MediaType.APPLICATION_JSON).content(request)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse responseBody = result.getResponse();

		assertEquals(HttpStatus.NO_CONTENT.value(), responseBody.getStatus());
	}

	@Test
	public void testMakeNewTransactionWithTransactionInFuture() throws Exception {

		Mockito.when(transactionsService.addNewTransaction(Mockito.any(TransactionEntity.class)))
				.thenReturn(Constants.HAPPENS_IN_FUTURE);
		String request = "{\r\n" + "	\"amount\":13.3,\r\n" + "	\"timestamp\":" + Instant.now() + 60000 * 120 + "\n"
				+ // timestamp after two hours
				"}";
		
		// Send course as body to v1/transactions
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/transactions")
				.accept(org.springframework.http.MediaType.APPLICATION_JSON).content(request)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse responseBody = result.getResponse();

		assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.getStatus());
	}

}
