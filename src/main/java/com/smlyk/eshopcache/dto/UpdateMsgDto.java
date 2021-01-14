package com.smlyk.eshopcache.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: always
 * @Date: 2021/1/6 5:21 下午
 */
@Data
@AllArgsConstructor
public class UpdateMsgDto implements Serializable {

    private String type;

    private String msg;
}
