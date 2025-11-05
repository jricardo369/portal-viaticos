package com.viaticos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.edicom.ediwinws.service.cfdi.GetCFDiStatus;
import com.edicom.ediwinws.service.cfdi.GetCFDiStatusResponse;
import com.viaticos.domain.Cfdi;

@Service
@Component("soapclient")
public class SoapClient extends WebServiceGatewaySupport{
	
	@Value("${soap.url}")
	private String urlSoap;

	@Value("${soap.user}")
	private String userSoap;

	@Value("${soap.pass}")
	private String passSoap;

	public GetCFDiStatusResponse getCfdiStatus(Cfdi cfdi) {
		
		GetCFDiStatus req = new GetCFDiStatus();
		GetCFDiStatusResponse response = new GetCFDiStatusResponse();
		
		req.setUser(userSoap);
		req.setPassword(passSoap);
		req.setRfcE(cfdi.getRfcEmisor());
		req.setRfcR(cfdi.getRfcReceptor());
		req.setTest(false);
		req.setTotal(cfdi.getTotal().doubleValue());
		req.setUuid(cfdi.getUuid());
		
		response = (GetCFDiStatusResponse) getWebServiceTemplate().marshalSendAndReceive(urlSoap, req);
		
		return response;
	}
	
}
