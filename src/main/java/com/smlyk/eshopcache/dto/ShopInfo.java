package com.smlyk.eshopcache.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: always
 * @Date: 2021/1/12 2:09 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopInfo {

    private Integer id;

    private String name;

    private Integer level;

    private Double goodCommentRate;

}
