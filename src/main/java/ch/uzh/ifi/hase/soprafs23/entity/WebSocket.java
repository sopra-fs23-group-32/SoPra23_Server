package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.WebSocketType;

public class WebSocket {
    private WebSocketType webSocketType;
    private Object load;

    public WebSocket(WebSocketType type, Object load){
        this.webSocketType = type;
        this.load = load;
    }

    public Object getLoad(){
        return load;
    }
    public void setLoad(Object load){
        this.load=load;
    }

    public WebSocketType getType(){
        return webSocketType;
    }
    public void setType(WebSocketType type){
        this.webSocketType = type;
    }

}


