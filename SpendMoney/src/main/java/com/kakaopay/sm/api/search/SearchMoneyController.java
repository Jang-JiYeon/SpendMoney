package com.kakaopay.sm.api.search;

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
public class SearchMoneyController {
	private static Logger logger = LogManager.getLogger(SearchMoneyController.class);
	
	@Autowired
    private SearchMoneySearch service;
	
	/*
	 * 조회 API
	 * 1. 뿌리기 시 발급된 token을 요청값으로 받음
	 * 2. token에 해당하는 뿌리기 건의 현재 상태를 3번 응답값으로 내려줌.
	 * 3. 뿌린 시간, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보([받은금액, 받은 사용자 아이디]리스트)
	 * 4. 뿌린 사람 자신만 조회 할 수 있음.
	 *    다름사람의 뿌리기 건은이나 유효하지 않은 token은 조회 실패 응답.
	 * 5. 뿌린 건에 대한 조회는 7일동안 가능
	 */
	@RequestMapping(value="/api/v1/searchMoney", method=RequestMethod.POST)
	public @ResponseBody SpendMoneyApiResponse searchMoney(HttpServletRequest request,
			@RequestBody HashMap<String, String> param) throws Exception {
        logger.info("---------------------------------------------------------------------");
        logger.info("   call url : /api/v1/searchMoney");
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
		
		//조회 기능 수행
		param.put("sendId", sendId);
		param.put("roomId", roomId);
		smApiResponse = service.searchMoneyProcess(param);
				
        return smApiResponse;
    }
}
