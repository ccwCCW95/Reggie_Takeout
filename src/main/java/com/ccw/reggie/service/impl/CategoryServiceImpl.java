package com.ccw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccw.reggie.common.CustomException;
import com.ccw.reggie.entity.Category;
import com.ccw.reggie.entity.Dish;
import com.ccw.reggie.entity.Setmeal;
import com.ccw.reggie.mapper.CategoryMapper;
import com.ccw.reggie.service.CategoryService;
import com.ccw.reggie.service.DishService;
import com.ccw.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<Dish>();
        // 添加查询条件 根据分类id进行查询
        lambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(lambdaQueryWrapper);
        // 查询当前分类是否关联了菜品
        if(count > 0){
            throw new CustomException("该分类下关联了菜品，不能删除！");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<Setmeal>();
        // 添加查询条件 根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);

        // 查询当前分类是否关联了套餐
        if(count1 > 0){
            throw new CustomException("该分类下关联了套餐，不能删除！");
        }
        // 正常删除
        super.removeById(id);
    }
}
