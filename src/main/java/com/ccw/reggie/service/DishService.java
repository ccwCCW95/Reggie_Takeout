package com.ccw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccw.reggie.dto.DishDto;
import com.ccw.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavors(DishDto dishDto);

    public DishDto getDishWithFlavors(Long id);

    public  void updateWithFlavors(DishDto dishDto);
}
