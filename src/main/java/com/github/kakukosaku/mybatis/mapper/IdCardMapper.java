package com.github.kakukosaku.mybatis.mapper;

import com.github.kakukosaku.mybatis.pojo.IdCard;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/31
 */
public interface IdCardMapper {

    IdCard selectIdCardById(int id);

}
