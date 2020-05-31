package com.github.kakukosaku.spring.service;


public interface ProductService {

    void browse(String loginName, String productName);

    void browseAgain(String loginName, String productName);

}
