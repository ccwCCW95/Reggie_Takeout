package com.ccw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccw.reggie.dto.SetmealDto;
import com.ccw.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
}
