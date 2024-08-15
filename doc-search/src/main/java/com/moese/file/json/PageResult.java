package com.moese.file.json;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import lombok.Data;
import lombok.ToString;


/**
 * Created by zxc on 2017/9/12.
 */
@Data
@ToString
public class PageResult<T> {

    public List<T> list;
    public Page<?> page;
}
