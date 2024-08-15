package com.moese.file.json;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by zxc on 2017/8/16.
 */

@Data
@ToString
@ApiModel
public class ActionMessage<T> {
    public static final int success=1;
    public static final int fail=0;
    public static final int unLogin= -1;
    @ApiModelProperty(value = "code=1表示成功，code=0表示失败")
    private int code;
    @ApiModelProperty("错误信息,当code=0的时候可以读取该错误信息")
    private String message;
    @ApiModelProperty("数据实体d")
    private T data;
    public static ActionMessage create(){
        ActionMessage am=new ActionMessage();
        return am;
    }
    public static ActionMessage fail(){
        ActionMessage am=create();
        am.setCode(ActionMessage.fail);
        return am;
    }

    public static ActionMessage success(){
        ActionMessage am=create();
        am.setCode(ActionMessage.success);
        return am;
    }

    public static ActionMessage fail(Exception e){
        ActionMessage am=create();
        am.setCode(ActionMessage.fail);
        am.setMessage(e.getMessage());
        return am;
    }

    public void setExceptionMessage(Exception e){
        this.setCode(ActionMessage.fail);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("发生错误异常,");
        stringBuffer.append(e.toString() + ":");
        StackTraceElement[] messages = e.getStackTrace();
        if(messages.length>0){
            stringBuffer.append(messages[0].toString());
            stringBuffer.append("\n");
        }
        this.setMessage(stringBuffer.toString());
    }
}
