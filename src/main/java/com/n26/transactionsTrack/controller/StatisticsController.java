package com.n26.transactionsTrack.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.transactionsTrack.entity.TransactionReportEntity;
import com.n26.transactionsTrack.service.TransactionsService;

@RestController
@RequestMapping("/v1/statistics")
public class StatisticsController {

	private final static Logger logger = LoggerFactory.getLogger(StatisticsController.class);
	
	@Autowired
	TransactionsService transactionsService;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<TransactionReportEntity> getLastMinuteReport() {
		TransactionReportEntity responsReport = transactionsService.getReport();
		logger.debug("generated report:" + responsReport);
		return new ResponseEntity<>(responsReport, HttpStatus.OK);
	}

}
