package com.lxy.openapi.web.master.controller;


import com.github.pagehelper.PageInfo;
import com.lxy.openapi.web.master.pojo.Customer;
import com.lxy.openapi.web.master.bean.TableData;
import com.lxy.openapi.web.master.service.CustomerService;
import com.lxy.openapi.web.master.bean.AjaxMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 客户管理
 */
@RestController
@RequestMapping("/sys/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;


    @RequestMapping("/add")
    public AjaxMessage add(Customer customer) {
        try {
            customer.setMoney(customer.getMoney()*10000);
            customerService.addCustomer(customer);
            return new AjaxMessage(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AjaxMessage(false, "添加失败");
    }


    @RequestMapping("/table")
    public TableData table(Customer customer, Integer page, Integer limit) {
        PageInfo<Customer> pageInfo = customerService.getCustomerList(customer, page, limit);
        return new TableData(pageInfo.getTotal(), pageInfo.getList());
    }

    @RequestMapping("/update")
    public AjaxMessage update(Customer Customer) {
        try {

            customerService.updateCustomer(Customer);
            return new AjaxMessage(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AjaxMessage(false, "更新失败");
    }

    @RequestMapping("/update2")
    public AjaxMessage update2(@RequestBody Customer Customer) {
        try {
            customerService.updateCustomer(Customer);
            return new AjaxMessage(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AjaxMessage(false, "更新失败");
    }

    @RequestMapping("/del")
    public AjaxMessage delete(int[] ids) {
        try {
            customerService.deleteCustomer(ids);
            return new AjaxMessage(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AjaxMessage(false, "删除失败");
    }

    @RequestMapping("/info")
    public Customer info(int id) {
        return customerService.getCustomerById(id);
    }

    @RequestMapping("/tree")//获取所有客户列表,添加应用时使用,实际开发中不需要
    public List<Customer> tree() {
        return customerService.getAllCustomer();
    }
}
