package com.dpnet.tools.qq.domain.model;

import javax.persistence.*;
import lombok.Data;

@Data
public class Switch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer flag;
}