package com.n26.transactionsTrack.controller;

import static org.junit.Assert.assertEquals;

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
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.n26.transactionsTrack.entity.TransactionEntity;
import com.n26.transactionsTrack.entity.TransactionReportEntity;
import com.n26.transactionsTrack.service.TransactionsService;
import com.n26.transactionsTrack.utils.Constants;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTests {

	private final static Logger logger = LoggerFactory.getLogger(StatisticsControllerTests.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private StatisticsController statisticsController;

	@MockBean
	private TransactionsService transactionsService;

	private static final TransactionReportEntity report = new TransactionReportEntity(Instant.now().getEpochSecond(), 44.40, 2.5, 111.25, 26.5, 8);

	@Before
	public void init() {
		
	}
	
	
	@Test
	public void testGetLastMinuteReport() throws Exception {

		Mockito.when(transactionsService.getReport()).thenReturn(report);
		// Send course as body to v1/transactions
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/statistics")
				.accept(org.springframework.http.MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse responseBody = result.getResponse();

		assertEquals(HttpStatus.OK.value(), responseBody.getStatus());

	}
}
