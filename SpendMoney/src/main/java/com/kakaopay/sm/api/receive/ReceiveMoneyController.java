package com.kakaopay.sm.api.receive;

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
public class ReceiveMoneyController {
	private static Logger logger = LogManager.getLogger(ReceiveMoneyController.class);
	
	@Autowired
    private ReceiveMoneyService service;
	
	/*
	 * 받기 API
	 * 1. 뿌리기 시 발급된 token을 요청값으로 받음
	 * 2. token에 해당하는 뿌리기 건 중 
	 *    아직 누구에게도 할당되지 않은 분배건 하나를 API를 호출한 사용자에게 할당하고,
	 *    그 금액을 응답값으로 내려줌 
	 * 3. 뿌리기 당 한 사용자는 한번만 받을 수 있음 
	 * 4. 자신이 뿌리기한 건은 자신이 받을 수 없음.
	 * 5. 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있음. 
	 * 6. 뿌린 건은 10분간만 유효. 10분이 지난 요청은 받기 실패 응답이 내려져야함.
	 */
	@RequestMapping(value="/api/v1/receiveMoney", method=RequestMethod.POST)
	public @ResponseBody SpendMoneyApiResponse receiveMoney(HttpServletRequest request,
			@RequestBody HashMap<String, String> param) throws Exception {
        logger.info("---------------------------------------------------------------------");
        logger.info("   call url : /api/v1/receiveMoney");
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
		
		//받기 기능 수행
		param.put("sendId", sendId);
		param.put("roomId", roomId);
		smApiResponse = service.receiveMoneyProcess(param);
				
        return smApiResponse;
    }
}
