package com.ccw.reggie.dto;

import com.ccw.reggie.entity.Setmeal;
import com.ccw.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
