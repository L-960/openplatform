package com.lxy.openapi.web.master.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lxy.openapi.web.master.mapper.CustomerMapper;
import com.lxy.openapi.web.master.mq.MqUtil;
import com.lxy.openapi.web.master.pojo.Customer;
import com.lxy.openapi.web.master.service.CustomerService;
import com.lxy.openapi.web.master.feign.CacheService;
import com.lxy.openplatform.commons.APIRoutingType;
import com.lxy.openplatform.commons.constans.SystemParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CacheService cacheService;

    //@Autowired
    //private MqUtil mqUtil;

    @Override
    public void addCustomer(Customer customer) throws Exception {
        customerMapper.insertCustomer(customer);
        try {
            cacheService.hMSet(SystemParams.CUSTOMER_REDIS_PRE + customer.getId(), customer);
            //发送mq到网关
            //mqUtil.sendMessage(SystemParams.CUSTOMER_REDIS_PRE + customer.getId(), APIRoutingType.ADD);
        } catch (Exception e) {
        }

    }

    @Override
    public PageInfo<Customer> getCustomerList(Customer criteria, int page, int limit) {
        PageHelper.startPage(page, limit);
        return new PageInfo<>(customerMapper.queryCustomer(criteria));
    }

    @Override
    public void updateCustomer(Customer customer) {
        customerMapper.updateCustomer(customer);
        try {
            cacheService.hMSet(SystemParams.CUSTOMER_REDIS_PRE + customer.getId(), customer);
            //发送mq到网关
            //mqUtil.sendMessage(SystemParams.CUSTOMER_REDIS_PRE + customer.getId(), APIRoutingType.UPDATE);
        } catch (Exception e) {
        }
    }

    @Override
    public void deleteCustomer(int[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        for (int id : ids) {
            Customer customer = customerMapper.getCustomerById(id);
            if (customer != null) {
                customer.setState(0);
                customerMapper.updateCustomer(customer);
                customerMapper.updateCustomer(customer);
                try {
                    cacheService.deleteKey(SystemParams.CUSTOMER_REDIS_PRE + customer.getId());
//                    //发送mq到网关
//                    mqUtil.sendMessage(SystemParams.CUSTOMER_REDIS_PRE + customer.getId(), APIRoutingType.DELETE);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public Customer getCustomerById(int id) {
        return customerMapper.getCustomerById(id);
    }

    @Override
    public List<Customer> getAllCustomer() {
        return customerMapper.getAllCustomer();
    }

    @Override
    public void addMoney(Integer money, int id) {
        Customer customer = customerMapper.getCustomerById(id);
        if (customer != null) {
            customer.setMoney(customer.getMoney() + money);

        }
    }
}
