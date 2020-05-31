package com.github.kakusosaku.spring.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.kakukosaku.spring.service.ProductService;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/30
 */
public class TestAnnotationAOP {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationAnnotationContext.xml");

        ProductService productService = (ProductService) ctx.getBean("productService");
        productService.browse("kaku", "github");

        ProductService productServiceAgain = (ProductService) ctx.getBean("productService");
        productServiceAgain.browseAgain("kaku", "github-again");
    }

}
