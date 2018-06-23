package com.n26.transactionsTrack.controller;

import java.time.Instant;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.transactionsTrack.Constants;
import com.n26.transactionsTrack.entity.TransactionEntity;
import com.n26.transactionsTrack.service.TransactionsService;

@RestController
@Validated
@RequestMapping("/v1/transactions")
public class TransactionController {

	private final static Logger logger = LoggerFactory.getLogger(TransactionController.class);

	@Autowired
	TransactionsService transactionService;
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public String test() {
		return "test";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<String> makeNewTransaction(@Valid @RequestBody TransactionEntity transaction) {
		int result = transactionService.addNewTransaction(transaction);
		
		if (result == Constants.BEFORE_LAST_MINUTE) {
			logger.info("this transaction occured more than one minute ago");
			return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
		} else if (result == Constants.ADD_WITHIN_LAST_MINUTE) {
			logger.info("this transaction added successfully to the counted list");
			return new ResponseEntity<>("", HttpStatus.CREATED);
		}
		// if the transaction is in the future
		else {
			logger.info("this transaction occured in the future");
			return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
		}
	}
	
}
