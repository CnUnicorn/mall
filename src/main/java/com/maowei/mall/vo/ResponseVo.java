package com.maowei.mall.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.maowei.mall.enums.ResponseEnum;
import org.springframework.validation.BindingResult;

import java.util.Objects;

//@JsonSerialize(include = )
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseVo<T> {

    private Integer status;

    private String msg;

    private T data;

    public ResponseVo(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ResponseVo<T> success(String msg) {
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), msg);
    }

    public static <T> ResponseVo<T> success() {
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getDesc());
    }

    public static <T> ResponseVo<T> error(ResponseEnum responseEnum) {
        return new ResponseVo<>(responseEnum.getCode(), responseEnum.getDesc());
    }

    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, String msg) {
        return new ResponseVo<>(responseEnum.getCode(), msg);
    }

    /**
     * 参数校验出现错误时，返回对应的错误码和错误信息
     * @param responseEnum 状态码和对应msg的枚举类
     * @param bindingResult 校验结果
     * @param <T>
     * @return
     */
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, BindingResult bindingResult) {
        return new ResponseVo<>(responseEnum.getCode(),
                Objects.requireNonNull(bindingResult.getFieldError()).getField() + " " + bindingResult.getFieldError().getDefaultMessage());
    }
}
