package com.dpnet.tools.qq.domain.model;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "app_baitiao")
public class AppBaitiao {
    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 白条用户状态：0，正在激活；1，激活成功；2，激活失败
     */
    private Byte status;

    /**
     * 信用额度，单位：元，长度20，精度2
     */
    private BigDecimal creditAmount;

    /**
     * 可用额度，单位：元，长度20，精度2
     */
    private BigDecimal remainingAmount;

    /**
     * 激活进度: 0,用户未填写个人信息 2,用户授权信息未全部完成 3,授权信息完成 33,审核中 34, 初筛通过 35,审核未通过 36,审核通过 37,未绑卡 38,绑卡中 39,激活成功
     */
    private Byte progress;

    /**
     * 更新时间
     */
    private Long updated;

    /**
     * 创建时间
     */
    private Long created;
}