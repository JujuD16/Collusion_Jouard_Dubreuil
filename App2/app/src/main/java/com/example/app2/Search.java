package com.example.app2;

public class Search {
    int _id;
    String _url;
    Integer _rate;
    public Search(){
    }


    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getUrl(){
        return this._url;
    }

    public void setUrl(String url){
        this._url = url;
    }

    public Integer getRate(){
        return this._rate;
    }

    public void setRate(Integer rate){
        this._rate = rate;
    }
}
