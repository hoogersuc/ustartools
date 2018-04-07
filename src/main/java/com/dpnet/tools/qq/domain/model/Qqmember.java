package com.dpnet.tools.qq.domain.model;

import javax.persistence.*;
import lombok.Data;

@Data
public class Qqmember {
    @Id
    private Long uin;

    private String card;

    private Integer flag;

    private Integer g;

    private Long join_time;

    private Long last_speak_time;

    private Integer level;

    private Integer point;

    private String nick;

    private Integer qage;

    private Integer role;

    private String tags;

    private Long groupid;
}