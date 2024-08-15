package com.moese.file.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 *   Title: ConvertResult.java
 *
 * @author zxc
 * @time 6/20/21 12:46 下午
 */
@Data
@ToString
@AllArgsConstructor
public class ConvertResult {

    private String targetFile;
    private boolean success;
}
