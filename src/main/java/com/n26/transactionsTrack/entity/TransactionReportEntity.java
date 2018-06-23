package com.n26.transactionsTrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.transactionsTrack.utils.CustomDoubleSerializer;

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
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private double max;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private double min;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private double sum;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private double avg;
	private long count;
	
	
	
}
