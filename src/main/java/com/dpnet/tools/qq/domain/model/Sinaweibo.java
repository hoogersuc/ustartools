package com.dpnet.tools.qq.domain.model;

import javax.persistence.*;
import lombok.Data;

@Data
public class Sinaweibo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uid;

    private String mid;

    private String headimg;

    private String nick;

    private String weibotxt;

    private String weiboimg;
}