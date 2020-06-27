package com.kakaopay.sm.api.send;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakaopay.sm.common.SpendMoneyApiResponse;

@Controller
public class SendMoneyController {
	
	private static Logger logger = LogManager.getLogger(SendMoneyController.class);
	
	@Autowired
    private SendMoneyService service;
	/*
	 * 공통사항
	 * 1. 뿌리기, 받기, 조회기능을 REST API로 구현
	 * 1-1. 요청한 사용자의 식별값은 숫자형태 이며 "X-USER-ID"라는 HTTP Header로 전달
	 * 1-2. 요청한 사용자가 속한 대화방의 식별값은 문자형태이며 "X-ROOM-ID"라는 HTTP Header로 전달
	 * 1-3. 모든 사용자는 뿌리기에 충분한 잔액을 보유하고 있다고 가정하여 별도로 잔액에 관련된 체크는 하지 않음.
	 * 2. 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에 문제가 없어야함.
	 * 3. 각 기능 및 제약사항에 대한 단위테스트를 반드시 작성.
	 */
	
	/*
	 * 뿌리기 API
	 * 1. 뿌릴금액, 뿌릴 인원을 요청값으로 받음
	 * 2. 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줌
	 * 3. 뿌릴 금액을 인원수에 맞게 분배하여 저장
	 * 4. token은 3자리 문자열로 구성되며 예측이 불가능해야 한다.
	 */
	@RequestMapping(value="/api/v1/sendMoney", method=RequestMethod.POST)
	public @ResponseBody SpendMoneyApiResponse sendMoney(HttpServletRequest request,
			@RequestBody HashMap<String, String> param) throws Exception {
        logger.info("---------------------------------------------------------------------");
        logger.info("   call url : /api/v1/sendMoney");
        logger.info("---------------------------------------------------------------------");
		SpendMoneyApiResponse smApiResponse = new SpendMoneyApiResponse();
    	
    	String sendId = request.getHeader("X-USER-ID");			//요청자 id
		String roomId = request.getHeader("X-ROOM-ID");			//요청자 대화방 id
		
		logger.info("sendId : " + sendId + ", roomId : " + roomId);
		
		if(StringUtils.isEmpty(sendId) || StringUtils.isEmpty(roomId)) {
			smApiResponse.setCode("0001");
			smApiResponse.setMessage("Header값을 확인 바랍니다.");
			return smApiResponse;
		}else if(!StringUtils.isNumeric(sendId) || !StringUtils.isAlpha(roomId)) {
			smApiResponse.setCode("0002");
			smApiResponse.setMessage("대화방ID는 문자만 가능하며, 요청자ID 숫자만 요청 가능합니다.");
			return smApiResponse;
		}
		
		//뿌리기 기능 수행
		param.put("sendId", sendId);
		param.put("roomId", roomId);
		smApiResponse = service.sendMoneyProcess(param);
		
        return smApiResponse;
    }
}
