package gov.gtas.services;

import java.util.List;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Pnr;
import gov.gtas.parsers.vo.MessageVo;

public class MessageDto {
	
	private Pnr pnr;
	private ApisMessage apis;
	private String rawMsg;
	private String msgType;
	private MessageVo msgVo;
	private List<String> rawMsgs;
	private String filepath;
	
	public MessageDto(Pnr pnr, ApisMessage apis, List<String> rawMsgs, String rawMsg, String msgType, MessageVo msgVo){
		this.pnr = pnr;
		this.apis = apis;
		this.msgType = msgType;
		this.msgVo = msgVo;
		this.rawMsg = rawMsg;
		this.rawMsgs = rawMsgs;
		this.filepath = filepath;
	}
	
	public MessageDto(){
		
	};

	public Pnr getPnr() {
		return pnr;
	}

	public void setPnr(Pnr pnr) {
		this.pnr = pnr;
	}

	public ApisMessage getApis() {
		return apis;
	}

	public void setApis(ApisMessage apis) {
		this.apis = apis;
	}

	public String getRawMsg() {
		return rawMsg;
	}

	public void setRawMsg(String rawMsg) {
		this.rawMsg = rawMsg;
	}

	public List<String> getRawMsgs() {
		return rawMsgs;
	}

	public void setRawMsgs(List<String> rawMsgs) {
		this.rawMsgs = rawMsgs;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public MessageVo getMsgVo() {
		return msgVo;
	}

	public void setMsgVo(MessageVo msgVo) {
		this.msgVo = msgVo;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
}