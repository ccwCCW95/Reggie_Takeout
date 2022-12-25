package com.ccw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccw.reggie.common.CustomException;
import com.ccw.reggie.dto.SetmealDto;
import com.ccw.reggie.entity.Setmeal;
import com.ccw.reggie.entity.SetmealDish;
import com.ccw.reggie.mapper.SetmealMapper;
import com.ccw.reggie.service.SetMealDishService;
import com.ccw.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 保存套餐 并保存套餐与菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 保存套餐与菜品的关联信息
        setMealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐以及对应的菜品
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态 确定是否能够被删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId, ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(lambdaQueryWrapper);

        // 若不能删除 抛出一个业务异常
        if(count > 0){
            throw new CustomException("该套餐还在售卖中，不能删除！");
        }

        // 如果可以被删除 先删除套餐表中的数据
        this.removeByIds(ids);

        // 删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setMealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}
