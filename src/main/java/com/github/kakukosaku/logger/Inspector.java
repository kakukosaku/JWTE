package com.github.kakukosaku.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 *
 * @author kaku
 * @date 2020-01-15
 */
class Inspector {

    private static final Logger LOG = LoggerFactory.getLogger(Inspector.class);

    public static void main(String[] args) {
        LOG.trace("Hello World!");
        LOG.debug("How are you today?");
        LOG.info("I am fine.");
        LOG.warn("I love programming.");
        LOG.error("I am programming.");
    }
}
