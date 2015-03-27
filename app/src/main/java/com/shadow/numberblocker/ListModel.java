package com.shadow.numberblocker;

/**
 * Created by nishi_000 on 2/20/2015.
 */
public class ListModel {
    private String number = "";
    private boolean chkMsg = false;
    private boolean chkCall = false;

    public ListModel(String msg, boolean chkMsg, boolean chkCall)
    {

        this.number = msg;
        this.chkMsg = chkMsg;
        this.chkCall = chkCall;
    }


    public String getNumber()
    {
        return this.number;
    }

    public void setMsg(boolean state)
    {
        this.chkMsg = state;
    }

    public void setCall(boolean state)
    {
        this.chkCall = state;
    }

    public boolean getMsg()
    {
        return this.chkMsg;
    }

    public boolean getCall()
    {
        return this.chkCall;
    }
}
