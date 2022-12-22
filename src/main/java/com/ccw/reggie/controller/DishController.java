package com.ccw.reggie.controller;

import com.ccw.reggie.common.R;
import com.ccw.reggie.dto.DishDto;
import com.ccw.reggie.service.DishFlavorService;
import com.ccw.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
