package com.kakaopay.sm.api.receive;

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
public class ReceiveMoneyService {
	private static Logger logger = LogManager.getLogger(ReceiveMoneyService.class);
	
	@Autowired
    private DefaultDao dao;
	
	//받기 기능 수행
    @SuppressWarnings("unchecked")
	public SpendMoneyApiResponse receiveMoneyProcess(@RequestBody HashMap<String, String> param) throws Exception {
    	SpendMoneyApiResponse smApiResponse = new SpendMoneyApiResponse();
    	
    	logger.info("토큰 : " + param.get("token"));
    	
		String sendCaseId = param.get("token");	//뿌리기 요청건의 토큰
		if(StringUtils.isEmpty(sendCaseId)) {
			smApiResponse.setCode("0005");
			smApiResponse.setMessage("토큰값을 입력바랍니다.");
			return smApiResponse;
		}
		
		//자신이 뿌리기한 건인지 체크
		int sendCount = (int) dao.select("com.kakaopay.sm.api.SpendMapper.checkIdentify", param);
		if (sendCount > 0) {
			smApiResponse.setCode("0008");
			smApiResponse.setMessage("자신이 뿌리기한 건은 자신이 받을 수 없습니다.");
            return smApiResponse;
        }
		
		//사용자가 이미 받은적 있는지 체크
		int recevieCount = (int) dao.select("com.kakaopay.sm.api.SpendMapper.checkReceiveHistory", param);
		if (recevieCount > 0) {
			smApiResponse.setCode("0009");
			smApiResponse.setMessage("뿌리기 당 사용자는 한번만 받을 수 있습니다.");
            return smApiResponse;
        }
		
		//뿌리기가 호출된 대화방과 동일한 대화방에 속한 요청건의 분배금액  조회
		List<SpendMoney> rsDivMoney = dao.selectList("com.kakaopay.sm.api.SpendMapper.selectDivMoney", param);
		if(rsDivMoney.size() == 0) {
			smApiResponse.setCode("0010");
			smApiResponse.setMessage("받을 수 있는 금액이 없습니다.");
            return smApiResponse;
		}
		
		//10분이 지난 요청건 받기 실패 응답(sendTime은 다 같으므로 하나라도 있으면 조회 불가)
		if(CommonUtil.getCurrentMinuteDifference(rsDivMoney.get(0).getSendTime()) > 10) {
			smApiResponse.setCode("0011");
			smApiResponse.setMessage("뿌린 후 10분이 지난 요청은 받을 수 없습니다.");
            return smApiResponse;
		}
		
		for(int i=0; i<rsDivMoney.size(); i++) {
			//한번도 받지 않았다면 할당되지 않은 분배건 하나를 응답값으로 전달
			smApiResponse.setData(rsDivMoney.get(i).getDivMoney());
			param.put("divMoney", rsDivMoney.get(i).getDivMoney());
		}
		
		//받기 완료 후 저장
		dao.update("com.kakaopay.sm.api.SpendMapper.updateReceiveDivMoney", param);
		
    	return smApiResponse;
    }
}
