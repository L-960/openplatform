package com.lxy.openplatform.commons.webmaster.pojo;





/**
*
*  @author author
*/
public class Customer  {
    public Customer() {
    }

    public Customer(Integer id, String username, String password, String nickname, Long money, String address, Integer state) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.money = money;
        this.address = address;
        this.state = state;
    }

    /**
    * 主键
    * 
    * isNullAble:0
    */
    private Integer id;

    /**
    * 公司名
    * isNullAble:1
    */
    private String username;

    /**
    * 
    * isNullAble:1
    */
    private String password;

    /**
    * 
    * isNullAble:1
    */
    private String nickname;

    /**
    * 金钱
    * isNullAble:1
    */
    private Long money;

    /**
    * 地址
    * isNullAble:1
    */
    private String address;

    /**
    * 状态
    * isNullAble:1
    */
    private Integer state;


    public void setId(Integer id){this.id = id;}

    public Integer getId(){return this.id;}

    public void setUsername(String username){this.username = username;}

    public String getUsername(){return this.username;}

    public void setPassword(String password){this.password = password;}

    public String getPassword(){return this.password;}

    public void setNickname(String nickname){this.nickname = nickname;}

    public String getNickname(){return this.nickname;}

    public void setMoney(Long money){this.money = money;}

    public Long getMoney(){return this.money;}

    public void setAddress(String address){this.address = address;}

    public String getAddress(){return this.address;}

    public void setState(Integer state){this.state = state;}

    public Integer getState(){return this.state;}
    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                "username='" + username + '\'' +
                "password='" + password + '\'' +
                "nickname='" + nickname + '\'' +
                "money='" + money + '\'' +
                "address='" + address + '\'' +
                "state='" + state + '\'' +
            '}';
    }


}
