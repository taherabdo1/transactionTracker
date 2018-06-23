package com.n26.transactionsTrack.service;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.n26.transactionsTrack.controller.StatisticsController;
import com.n26.transactionsTrack.entity.TransactionEntity;
import com.n26.transactionsTrack.entity.TransactionReportEntity;
import com.n26.transactionsTrack.utils.Constants;

@Service
public class TransactionsService implements TransactionsServiceInt {

	private final static Logger logger = LoggerFactory.getLogger(TransactionsService.class);

	@PostConstruct
	public void init() {
		for (int i = 0; i < 60; i++) {
			lastMinuteTransactions.add(i, new TransactionReportEntity());
		}
	}

	private ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private List<TransactionReportEntity> lastMinuteTransactions = new ArrayList<>(60); // entry for each second

	@Override
	public int addNewTransaction(TransactionEntity transactionEntity) {

		if (Instant.now().toEpochMilli() - transactionEntity.getTimestamp() > 60000) {
			return Constants.BEFORE_LAST_MINUTE;
		} else if (Instant.now().toEpochMilli() - transactionEntity.getTimestamp() <= 60000 && Instant.now().toEpochMilli() - transactionEntity.getTimestamp() >= 0) {
			Lock writeLock = rwLock.writeLock();
			writeLock.lock();
			try {
				Date date = Date.from(Instant.ofEpochMilli(transactionEntity.getTimestamp())); //
				TransactionReportEntity curerntSecondTotalReport = lastMinuteTransactions.get(date.getSeconds());
				// if this second holds data before
				if (curerntSecondTotalReport.getTimestampInSecond() == Instant
						.ofEpochMilli(transactionEntity.getTimestamp()).getEpochSecond()) {
					logger.debug("added the new transaction to the last minute transactions"+transactionEntity.toString());
					if (curerntSecondTotalReport.getMax() <= transactionEntity.getAmount()) {
						curerntSecondTotalReport.setMax(transactionEntity.getAmount());
					}
					if (curerntSecondTotalReport.getMin() >= transactionEntity.getAmount()) {
						curerntSecondTotalReport.setMin(transactionEntity.getAmount());
					}
					curerntSecondTotalReport.setSum(curerntSecondTotalReport.getSum() + transactionEntity.getAmount());
					curerntSecondTotalReport.setCount(curerntSecondTotalReport.getCount() + 1);
				} else {
					curerntSecondTotalReport.setTimestampInSecond(
							Instant.ofEpochMilli(transactionEntity.getTimestamp()).getEpochSecond());
					curerntSecondTotalReport.setCount(1);
					curerntSecondTotalReport.setMax(transactionEntity.getAmount());
					curerntSecondTotalReport.setMin(transactionEntity.getAmount());
					curerntSecondTotalReport.setSum(transactionEntity.getAmount());
					curerntSecondTotalReport.setAvg(0.0);
					
				}

			} finally {
				writeLock.unlock();
			}
			return Constants.ADD_WITHIN_LAST_MINUTE;
		} else {
			return Constants.HAPPENS_IN_FUTURE;
		}

	}

	@Override
	public TransactionReportEntity getReport() {

		TransactionReportEntity report = new TransactionReportEntity();
		report.setSum(0);
		report.setCount(0);
		report.setMax(Double.MIN_VALUE);
		report.setMin(Double.MAX_VALUE);
		report.setAvg(0);

		Lock readLock = rwLock.readLock();
		readLock.lock();
		logger.debug("generating the report...");
		try {
			long endSecond = Instant.now().getEpochSecond();
			for (int i = 0; i < lastMinuteTransactions.size(); i++) {
				if (endSecond - (lastMinuteTransactions.get(i).getTimestampInSecond()) < 60) {
					// if the record is within the last minutes
					report.setSum(report.getSum() + lastMinuteTransactions.get(i).getSum());
					report.setCount(report.getCount() + lastMinuteTransactions.get(i).getCount());
					if (report.getMax() < lastMinuteTransactions.get(i).getMax()) {
						report.setMax(lastMinuteTransactions.get(i).getMax());
					}
					if (report.getMin() > lastMinuteTransactions.get(i).getMin()||report.getMin() == 0.0) {
						report.setMin(lastMinuteTransactions.get(i).getMin());
					}
				}
			}
			report.setAvg(report.getSum() / report.getCount());
			// no transactions in last 60 seconds
			if (report.getCount() == 0) {
				report.setAvg(0);
				report.setCount(0);
				report.setMax(0);
				report.setMin(0);
				report.setSum(0);
			}			
			logger.debug(report.toString());
			return report;
		} finally {
			readLock.unlock();
		}
	}

}
