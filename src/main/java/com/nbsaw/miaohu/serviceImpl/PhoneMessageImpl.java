package com.nbsaw.miaohu.serviceImpl;

import com.nbsaw.miaohu.service.PhoneMessageService;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PhoneMessageImpl implements PhoneMessageService {
    // 日志设置
    private final static Logger logger = Logger.getLogger(PhoneMessageImpl.class);

    // 验证码的配置
    private final String URL = "http://gw.api.taobao.com/router/rest"; // api地址
    private final String APPKEY = "23737153";
    private final String SECRET = "96b6c075231e49e77cf951efd6dca529";

    // 将生成的验证码发送到手机，并且返回验证码的结果
    @Override
    public String sendRegisterCode(String phone) {
        String random = String.valueOf((int)((Math.random()*9+1)*100000)); //随机数生成
        TaobaoClient client = new DefaultTaobaoClient(URL, APPKEY, SECRET);
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType("normal"); // 消息类型默认
        req.setSmsFreeSignName("彭佳文"); // 签名
        req.setSmsParamString("{number:'"+random+"'}"); // 六位随机验证码
        req.setRecNum(phone); // 发送到号码
        req.setSmsTemplateCode("SMS_60000851"); // 短信验证模板ID
        AlibabaAliqinFcSmsNumSendResponse rsp;
        try {
            rsp = client.execute(req);
            logger.info(rsp.getBody());
        } catch (ApiException e) {
            logger.error(e.getMessage());
        }
        return random;
    }
}
