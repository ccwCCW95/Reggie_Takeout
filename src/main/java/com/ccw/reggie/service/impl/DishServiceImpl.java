package com.ccw.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccw.reggie.dto.DishDto;
import com.ccw.reggie.entity.Dish;
import com.ccw.reggie.entity.DishFlavor;
import com.ccw.reggie.mapper.DishMapper;
import com.ccw.reggie.service.DishFlavorService;
import com.ccw.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品 同时保存口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavors(DishDto dishDto) {
        // 保存菜品基本信息到菜品表
        this.save(dishDto);

        Long dishId = dishDto.getId();

        // 得到菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存菜品口味到菜品口味表中
        dishFlavorService.saveBatch(flavors);
    }
}
