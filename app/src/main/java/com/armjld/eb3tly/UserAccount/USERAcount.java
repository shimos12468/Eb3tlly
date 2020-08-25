package com.armjld.eb3tly.UserAccount;

public class USERAcount implements goldAccount,premimAccount,diamondAccount{

    private String name;
    private String id ,SSNPIC , PROfilepic ,defultpic,joindate ,Password;

    public USERAcount(String name, String id, String SSNPIC, String PROfilepic, String defultpic, String joindate, String password) {
        this.name = name;
        this.id = id;
        this.SSNPIC = SSNPIC;
        this.PROfilepic = PROfilepic;
        this.defultpic = defultpic;
        this.joindate = joindate;
        Password = password;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSSNPIC() {
        return SSNPIC;
    }

    public void setSSNPIC(String SSNPIC) {
        this.SSNPIC = SSNPIC;
    }

    public String getPROfilepic() {
        return PROfilepic;
    }

    public void setPROfilepic(String PROfilepic) {
        this.PROfilepic = PROfilepic;
    }

    public String getDefultpic() {
        return defultpic;
    }

    public void setDefultpic(String defultpic) {
        this.defultpic = defultpic;
    }

    public String getJoindate() {
        return joindate;
    }

    public void setJoindate(String joindate) {
        this.joindate = joindate;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    @Override
    public void functionONE() {

    }

    @Override
    public void functiontwo() {

    }

    @Override
    public void functionthree() {

    }

    @Override
    public void functionONEp() {

    }

    @Override
    public void functiontwop() {

    }

    @Override
    public void functionthreep() {

    }

    @Override
    public void functionONEd() {

    }

    @Override
    public void functiontwod() {

    }

    @Override
    public void functionthreed() {

    }
}
