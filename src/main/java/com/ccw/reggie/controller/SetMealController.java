package com.ccw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccw.reggie.common.R;
import com.ccw.reggie.dto.SetmealDto;
import com.ccw.reggie.entity.Setmeal;
import com.ccw.reggie.service.CategoryService;
import com.ccw.reggie.service.SetMealDishService;
import com.ccw.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);

        return R.success("成功保存套餐信息！");
    }

    /**
     * 分页得到套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        // 构造分页器
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

        // 构造查询器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        // 执行查询
        setmealService.page(setmealPage, lambdaQueryWrapper);

        // 复制对象
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        // 添加分类名称
        List<Setmeal> setmealList = setmealPage.getRecords();

        List<SetmealDto> setmealDtoList = setmealList.stream().map((item) -> {
            String categoryName = categoryService.getById(item.getCategoryId()).getName();
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setCategoryName(categoryName);

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }
}
