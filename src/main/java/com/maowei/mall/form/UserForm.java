package com.maowei.mall.form;

import javax.validation.constraints.NotBlank;

public class UserForm {


//    @NotEmpty // 不能为null，长度必须大于0，一般用于判断集合是否为空
//    @NotNull  // 不能为null，但可以为empty
    @NotBlank // 只能用于String，并且trim()后长度要大于0，即不能有空格
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
