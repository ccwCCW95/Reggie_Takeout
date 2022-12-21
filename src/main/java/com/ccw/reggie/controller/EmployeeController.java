package com.ccw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccw.reggie.common.R;
import com.ccw.reggie.entity.Employee;
import com.ccw.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        // 1. 对拿到的密码进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据页面提交的username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 如果没有查询到 返回失败
        if(emp == null){
            return R.error("登录失败！");
        }

        // 4. 比对密码
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误！");
        }

        // 5. 查看员工状态
        if(emp.getStatus() == 0){
            return R.error("员工已被禁用！");
        }

        // 6. 登录成功 放入session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 清空session中的用户Id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工 信息为：{}", employee.toString());

        // 设置初始密码 并进行加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 设置时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        // 设置用户
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.save(employee);

        return R.success("新增用户成功！");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
//        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));
//        employee.setUpdateTime(LocalDateTime.now());

        Long id = Thread.currentThread().getId();
        log.info("当前线程id为：{}", id);

        employeeService.updateById(employee);

        return R.success("成功修改员工状态！");
    }

    /**
     * 根据id查找用户信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查找用户信息...");

        Employee employee = employeeService.getById(id);

        if(employee != null){
            return R.success(employee);
        }

        return R.error("未查询到该员工的信息！");
    }
}
