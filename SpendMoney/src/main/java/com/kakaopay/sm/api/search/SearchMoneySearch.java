package com.kakaopay.sm.api.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.kakaopay.sm.api.SpendMoney;
import com.kakaopay.sm.common.SpendMoneyApiResponse;
import com.kakaopay.sm.mybatis.DefaultDao;
import com.kakaopay.sm.util.CommonUtil;

@Service
public class SearchMoneySearch {
	private static Logger logger = LogManager.getLogger(SearchMoneySearch.class);
	
	@Autowired
    private DefaultDao dao;
	
	//조회 기능 수행
	@SuppressWarnings("unchecked")
	public SpendMoneyApiResponse searchMoneyProcess(@RequestBody HashMap<String, String> param) throws Exception {
    	SpendMoneyApiResponse smApiResponse = new SpendMoneyApiResponse();
    	
    	logger.info("토큰 : " + param.get("token"));
    	
		String sendCaseId = param.get("token");	//뿌리기 요청건의 토큰
		if(StringUtils.isEmpty(sendCaseId)) {
			smApiResponse.setCode("0005");
			smApiResponse.setMessage("토큰값을 입력바랍니다.");
			return smApiResponse;
		}
		
		//자신이 뿌리기한 건인지 체크
		List<SpendMoney> rsSendMoney = dao.selectList("com.kakaopay.sm.api.SpendMapper.selectSendMoney", param);
		if(rsSendMoney.size() == 0) {
			smApiResponse.setCode("0006");
			smApiResponse.setMessage("조회 실패하였습니다.");
            return smApiResponse;
		}
		
		//7일 지난 건은 조회 불가(sendTime은 다 같으므로 하나라도 있으면 조회 불가)
		if(CommonUtil.getCurrentDayDifference(rsSendMoney.get(0).getSendTime()) > 7) {
			smApiResponse.setCode("0007");
			smApiResponse.setMessage("7일 지난 건은 조회 불가입니다.");
			return smApiResponse;
		}
		
		//token에 해당하는 뿌리기 건의 현재 상태정보 구성
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("sendTime", rsSendMoney.get(0).getSendTime());
		result.put("sendMoney", rsSendMoney.get(0).getSendMoney());
		int receiveMoney = 0;	//받은 완료된 금액
		
		ArrayList<HashMap<String, String>> receiveDataList = new ArrayList<HashMap<String, String>>();
		for(int i=0; i<rsSendMoney.size(); i++) {
			if(!StringUtils.isEmpty(rsSendMoney.get(i).getReceiveYn())) {
				if(rsSendMoney.get(i).getReceiveYn().equals("Y")) {		//받은 상태의 금액들만 더해주고 정보구성
					receiveMoney += Integer.parseInt(rsSendMoney.get(i).getDivMoney());
					HashMap<String, String> receiveData = new HashMap<String, String>();
					receiveData.put("divMoney", rsSendMoney.get(i).getDivMoney());
					receiveData.put("receiveId", rsSendMoney.get(i).getReceiveId());
					receiveDataList.add(receiveData);
				}
			}
		}
		result.put("receiveMoney", receiveMoney);
		result.put("receiveData", receiveDataList);
		
		smApiResponse.setData(result);
		
    	return smApiResponse;
    }
}
