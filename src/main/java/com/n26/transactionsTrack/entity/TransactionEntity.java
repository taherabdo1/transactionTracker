package com.n26.transactionsTrack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {

    @JsonProperty(value = "amount", required = true)
	double amount;
    @JsonProperty(value = "timestamp", required = true)
	long timestamp;
	@Override
	public String toString() {
		return "TransactionEntity [amount=" + amount + ", timestamp=" + timestamp + "]";
	}


}
