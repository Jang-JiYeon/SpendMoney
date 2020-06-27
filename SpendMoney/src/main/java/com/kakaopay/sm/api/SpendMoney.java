package com.kakaopay.sm.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SpendMoney {
	private String roomId;
	private String sendId;
	private String sendCaseId;
	private String sendMoney;
	private String sendTime;
	private String divMoney;
	private String receiveId;
	private String receiveYn;
	private String receiveTime;
}
