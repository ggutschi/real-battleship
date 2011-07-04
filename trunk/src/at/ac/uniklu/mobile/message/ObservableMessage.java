package at.ac.uniklu.mobile.message;

/**
 * this class describes the structure of a message for implementing the observer pattern 
 * (exchange of messages between observer and observable object)
 *
 */
public class ObservableMessage {
	public enum MessageIntend {SCORE_INCREMENT, SCORE_DECREMENT};
	public Object messageContent;
	public MessageIntend messageIntend;
	
	public ObservableMessage(MessageIntend pMessageIntend, Object pMessageContent) {
		messageIntend = pMessageIntend;
		messageContent = pMessageContent;
	}
	
	public Object getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(Object messageContent) {
		this.messageContent = messageContent;
	}

	public MessageIntend getMessageIntend() {
		return messageIntend;
	}

	public void setMessageIntend(MessageIntend messageIntend) {
		this.messageIntend = messageIntend;
	}

	
	
	
	
	

}
