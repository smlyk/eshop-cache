package com.smlyk.eshopcache.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: always
 * @Date: 2021/1/6 5:32 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfo implements Serializable {

    private Integer id;

    private String name;

    private String color;

    private double price;

    private String pictureList;

    private String specification;

    private String service;

    private String size;

    private Integer shopId;

    private String modifiedTime;

}
