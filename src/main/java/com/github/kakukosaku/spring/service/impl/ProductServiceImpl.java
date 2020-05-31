package com.github.kakukosaku.spring.service.impl;

import java.util.concurrent.TimeUnit;

import com.github.kakukosaku.spring.service.ProductService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Description
 *
 * @author kaku
 * Date    2020/5/30
 */
@Component("productService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductServiceImpl implements ProductService {

    int countDown = 3;

    @Override
    public void browse(String loginName, String productName) {
        while (countDown > 0) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDown--;
        }
        System.out.println(loginName + " view " + productName);
    }

    public void browseAgain(String loginName, String productName) {
        while (countDown > 0) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDown--;
        }
        System.out.println(loginName + " view " + productName);
    }

}
