package com.kakaopay.sm.api.send;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestBody;

import com.kakaopay.sm.common.SpendMoneyApiResponse;
import com.kakaopay.sm.mybatis.DefaultDao;

@Service
public class SendMoneyService {
	private static Logger logger = LogManager.getLogger(SendMoneyService.class);

    @Autowired
    private DefaultDao dao;
    
    @Autowired
    private DataSourceTransactionManager transactionManager;
    
    //뿌리기 기능 수행
    public SpendMoneyApiResponse sendMoneyProcess(@RequestBody HashMap<String, String> param) throws Exception {
    	SpendMoneyApiResponse smApiResponse = new SpendMoneyApiResponse();
    	
		String sendPsersonNum = param.get("sendPsersonNum");	//뿌릴 인원
		String sendTotalMoney = param.get("sendTotalMoney");	//뿌릴 금액

		logger.info("뿌릴 인원 : " + param.get("sendPsersonNum") + ", 뿌릴 금액 : " + param.get("sendTotalMoney"));
		
		if( StringUtils.isEmpty(sendPsersonNum) || StringUtils.isEmpty(sendTotalMoney)) {
			smApiResponse.setCode("0003");
			smApiResponse.setMessage("인원과 금액을 입력바랍니다.");
			return smApiResponse;
		}else if(!StringUtils.isNumeric(sendPsersonNum) || !StringUtils.isNumeric(sendTotalMoney)) {
			smApiResponse.setCode("0004");
			smApiResponse.setMessage("인원과 금액은 숫자만 입력 가능합니다.");
			return smApiResponse;
		}
		
		//인원수에 맞게 분배
		int[] divisionMoney = divisionSendMoney(sendPsersonNum, sendTotalMoney);
		//요청건 고유 token 3자리
		StringBuffer sendCaseToken = createSendCaseToken();
		
		//요청자 정보
		HashMap<String, String> sendData = new HashMap<String, String>();
		sendData.put("roomId", param.get("roomId"));
		sendData.put("sendId", param.get("sendId"));
		sendData.put("sendCaseId", sendCaseToken.toString());
		sendData.put("sendMoney", sendTotalMoney);
		
		logger.info(sendData);
		
		//분배된 돈 정보
		List<HashMap<String, String>> receiveData = new ArrayList<HashMap<String, String>>();
		for(int i=0; i<divisionMoney.length; i++) {
			HashMap<String, String> receiveTemp = new HashMap<String, String>();
			receiveTemp.put("roomId", param.get("roomId"));
			receiveTemp.put("sendId", param.get("sendId"));
			receiveTemp.put("sendCaseId", sendCaseToken.toString());
			receiveTemp.put("divMoney", String.valueOf(divisionMoney[i]));
			
			receiveData.add(receiveTemp);
		}
		
		//요청자 금액 저장
		insertSendMoney(sendData, receiveData);
		//응답값에 고유 3자리 token
		smApiResponse.setData(sendCaseToken);
		
    	return smApiResponse;
    }
    
    //뿌릴 금액 인원수에 맞게 분할
    public int[] divisionSendMoney(String sendPsersonNum, String sendTotalMoney) {
		int personNum = Integer.parseInt(sendPsersonNum);	//뿌릴 인원
		int sendMoney = Integer.parseInt(sendTotalMoney);	//뿌릴 금액
		int remainMoney = sendMoney;				//남은돈
		int[] divisionMoney = new int[personNum];	//분배한돈
		
		for(int i=0; i<personNum; i++) {
			if(i != (personNum-1)) {
				int randomNum = (int)(Math.random() * remainMoney) + 1;	//남은돈 안에서 랜덤하게 분할
				remainMoney = remainMoney - randomNum;
				divisionMoney[i] = randomNum;	//랜덤한 돈 전달
				//logger.info(randomNum);
				continue;
			}
			divisionMoney[i] = remainMoney;		//마지막엔 남은돈을 전달
		}
		logger.info("남은돈 : " + Arrays.toString(divisionMoney));
		
		return divisionMoney;
    }
    
	//뿌리기 요청건 고유한 문자열 3자리 token 발급
    public StringBuffer createSendCaseToken() {
    	StringBuffer sendCaseToken = new StringBuffer();
		Random randomNum = new Random();
		for(int i=0; i<3; i++) {	//대소문자,숫자가 섞인 고유한 3개의 문자열
			int randomIdx = randomNum.nextInt(3);	//숫자,소문자,대문자 구분할 랜덤숫자

			switch(randomIdx) {
			case 0:	//a-z
				sendCaseToken.append((char)('a' + randomNum.nextInt('z'-'a')));
				break;
			case 1: //A-Z
				sendCaseToken.append((char)('A' + randomNum.nextInt('Z'-'A')));
				break;
			case 2: //0-9
				sendCaseToken.append(randomNum.nextInt(10));
				break;
			}
		}
		logger.info("3자리 token : " + sendCaseToken);
		
		return sendCaseToken;
    }
    
    //요청자 정보와 분배된 금액의 정보 저장
    public void insertSendMoney(HashMap<String, String> sendData, List<HashMap<String, String>> receiveData) throws Exception {
    	//insert 도중 Error 발생시 rollback
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("example-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try {
            //요청자의 정보 저장
            dao.insert("com.kakaopay.sm.api.SpendMapper.insertSendMaster", sendData);
            //분배된 금액의 정보 저장
            dao.insert("com.kakaopay.sm.api.SpendMapper.insertReceiveMaster", receiveData);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
        
        transactionManager.commit(status);
    }
}
