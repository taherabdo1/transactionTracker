package com.n26.transactionsTrack.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.n26.transactionsTrack.entity.TransactionEntity;
import com.n26.transactionsTrack.entity.TransactionReportEntity;
import com.n26.transactionsTrack.utils.Constants;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTests {

	private final static Logger logger = LoggerFactory.getLogger(TransactionServiceTests.class);

	@InjectMocks
	TransactionsService transactionsService;

	final private TransactionEntity transactionEntityWithinLastMinute = new TransactionEntity();
	final private TransactionEntity transactionEntityBeforeLastMinute = new TransactionEntity();
	final private TransactionEntity transactionEntityInFuture = new TransactionEntity();
	final private List<TransactionReportEntity> transactions = new ArrayList<>();
	private TransactionReportEntity second_1_report = null;
	private TransactionReportEntity second_30_report = null;

	@Before
	public void init() {
		transactionsService.init();
		transactionEntityWithinLastMinute.setAmount(31.4);
		transactionEntityWithinLastMinute.setTimestamp(Instant.now().toEpochMilli());

		transactionEntityBeforeLastMinute.setAmount(31.4);
		transactionEntityBeforeLastMinute.setTimestamp(Instant.now().toEpochMilli() - (60000 * 5));

		transactionEntityInFuture.setAmount(20.75);
		transactionEntityInFuture.setTimestamp(Instant.now().toEpochMilli() + (60000 * 5));

		// transactions within the last minute, max ->36.60
		second_1_report = new TransactionReportEntity(Instant.now().getEpochSecond(), 34.60, 12.8, 99.5, 8.0, 4);
		second_30_report = new TransactionReportEntity(Instant.now().getEpochSecond() - 30, 36.60, 12.8, 99.5, 8.0, 4);
		transactions.add(second_1_report);
		transactions.add(second_30_report);

	}

	@Test
	public void testAddNewTransactionWithinLastMinute() {
		int result = transactionsService.addNewTransaction(transactionEntityWithinLastMinute);
		assertEquals(Constants.ADD_WITHIN_LAST_MINUTE, result);
	}

	@Test
	public void testAddNewTransactionBeforeLastMinute() {
		int result = transactionsService.addNewTransaction(transactionEntityBeforeLastMinute);
		assertEquals(Constants.BEFORE_LAST_MINUTE, result);
	}

	@Test
	public void testAddNewTransactionInFuture() {
		int result = transactionsService.addNewTransaction(transactionEntityInFuture);
		assertEquals(Constants.HAPPENS_IN_FUTURE, result);
	}

	@Test
	public void testGetReportHappeyScienario()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {

		Field field = TransactionsService.class.getDeclaredField("lastMinuteTransactions");
		field.setAccessible(true);
		field.set(transactionsService, transactions);
		TransactionReportEntity result = transactionsService.getReport();
		assertEquals(result.getSum(), second_1_report.getSum() + second_30_report.getSum(), 0.001);
	}

	@Test
	public void testGetReportIfThereIsRecordsBeforeLastMinute()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		// transaction 3 minutes ago, should not be counted in the report
		transactions.add(
				new TransactionReportEntity(Instant.now().getEpochSecond() - (60000 * 3), 34.60, 12.8, 99.5, 8.0, 4));

		Field field = TransactionsService.class.getDeclaredField("lastMinuteTransactions");
		field.setAccessible(true);
		field.set(transactionsService, transactions);
		TransactionReportEntity result = transactionsService.getReport();
		assertEquals(result.getSum(), second_1_report.getSum() + second_30_report.getSum(), 0.001);
	}

	@Test
	public void testGetReportIfThereIsNoRecordsLastMinute()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field field = TransactionsService.class.getDeclaredField("lastMinuteTransactions");
		field.setAccessible(true);
		field.set(transactionsService, new ArrayList<>());
		TransactionReportEntity result = transactionsService.getReport();
		assertEquals(result.getSum(), 0.0, 0.001);
	}

}
