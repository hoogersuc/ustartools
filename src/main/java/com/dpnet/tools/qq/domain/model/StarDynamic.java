package com.dpnet.tools.qq.domain.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "star_dynamic")
public class StarDynamic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String unionid;

    private String dynamic_img;

    private String location;

    private BigDecimal lat;

    private BigDecimal lng;

    private Date createtime;

    private String dynamic_txt;

    private String comments;

    private String praises;

    private String commentscopy;
}