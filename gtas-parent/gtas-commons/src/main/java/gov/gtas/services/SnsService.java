package gov.gtas.services;

public interface SnsService {
	
	public String sendMessage(String message);
	
	public String sendNotification(String message);
}
