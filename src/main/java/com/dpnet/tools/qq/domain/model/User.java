package com.dpnet.tools.qq.domain.model;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Data;

@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String tel;

    private String identity;

    private String picture1;

    private String picture2;

    private Integer level;

    private String certs;

    private String uid;

    private Integer star_state;

    private String nick_name;

    private String occupation;

    private String head_img;

    private String openid;

    private String holdcard;

    private String minopenid;

    private String unionid;

    private Integer gender;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    private String alipay_no;
}