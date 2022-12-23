package com.ccw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccw.reggie.dto.DishDto;
import com.ccw.reggie.entity.Dish;
import com.ccw.reggie.entity.DishFlavor;
import com.ccw.reggie.mapper.DishMapper;
import com.ccw.reggie.service.DishFlavorService;
import com.ccw.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
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

    /**
     * 根据id查询具体的菜品和口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getDishWithFlavors(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);

        // 构造查询器对象
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());

        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(flavors);


        return dishDto;
    }

    /**
     * 根据菜品id更新菜品信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavors(DishDto dishDto) {
        // 更新dish表对应基本信息
        this.updateById(dishDto);
        // 清理当前菜品对应的口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(lambdaQueryWrapper);
        // 添加当前新的口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
