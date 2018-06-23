package com.n26.transactionsTrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionReportEntity {

	@JsonIgnore
	private long timestampInSecond;
	private double max;
	private double min;
	private double sum;
	private double avg;
	private long count;
	
	
	
}
