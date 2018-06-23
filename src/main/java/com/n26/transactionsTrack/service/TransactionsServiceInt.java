package com.n26.transactionsTrack.service;

import com.n26.transactionsTrack.entity.TransactionEntity;
import com.n26.transactionsTrack.entity.TransactionReportEntity;

public interface TransactionsServiceInt {

	int addNewTransaction(TransactionEntity transactionEntity);
	TransactionReportEntity getReport();
}
