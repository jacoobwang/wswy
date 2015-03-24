package com.example.entity;


public class ChatMsgEntity {
	private String name;
	private String date;
    private String text;
    private String time;
    private boolean isComMeg = true;
    private String image;
    
    public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public Boolean getisComMeg() {
		return isComMeg;
	}

	public void setisComMeg(Boolean isComMeg) {
		this.isComMeg = isComMeg;
	}
	
	public ChatMsgEntity(){
		
	}
	
	public ChatMsgEntity(String name, String date, String text, boolean isComMsg, String image){
		 this.name = name;
		 this.date = date;
		 this.text = text;
		 this.isComMeg = isComMsg;
		 this.image = image;
	 }
}
