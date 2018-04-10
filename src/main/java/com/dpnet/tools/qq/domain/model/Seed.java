package com.dpnet.tools.qq.domain.model;

import javax.persistence.*;
import lombok.Data;

@Data
public class Seed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String url;
}