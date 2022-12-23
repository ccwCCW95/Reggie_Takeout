package com.ccw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccw.reggie.common.R;
import com.ccw.reggie.dto.DishDto;
import com.ccw.reggie.entity.Category;
import com.ccw.reggie.entity.Dish;
import com.ccw.reggie.service.CategoryService;
import com.ccw.reggie.service.DishFlavorService;
import com.ccw.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody  DishDto dishDto){
        dishService.saveWithFlavors(dishDto);
        return R.success("新增菜品成功！");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(int page, int pageSize, String name){
        // 构造分页器对象
        Page<Dish> pageInfo = new Page<Dish>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 构造查询器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<Dish>();
        // 过滤条件
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        // 排序
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        // 查询
        dishService.page(pageInfo, lambdaQueryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getDishWithFlavors(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody  DishDto dishDto){
        dishService.updateWithFlavors(dishDto);
        return R.success("修改菜品成功！");
    }
}
