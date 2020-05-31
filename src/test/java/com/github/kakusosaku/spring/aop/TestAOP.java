package com.github.kakusosaku.spring.aop;

import com.github.kakukosaku.spring.service.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/30
 */
public class TestAOP {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        ProductService productService = (ProductService) ctx.getBean("productService");

        productService.browse("kaku", "github");
    }

}
