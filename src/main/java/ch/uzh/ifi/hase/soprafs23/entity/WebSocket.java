package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.WebScoketType;
public class WebScoket {
    private WebScoketType type;
    private Object load;

    public WebScoket(WebScoketType type, Object load){
        this.type=type;
        this.load=load;
    }

public WebScoket(){
}

public Object getLoad(){
    return load;
}

public void setLoad(Object load){
    this.load=load;
}

public WebScoketType getType(){
    return this.type=type;
}

public void setType(WebScoketType type){
    this.type=type;
}

}


