package com.dpnet.tools.qq.domain.model;

import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "url_history")
public class UrlHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String url;
}