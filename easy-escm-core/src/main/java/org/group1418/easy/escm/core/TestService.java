package org.group1418.easy.escm.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



/**
 * @author yq 2024/1/13 12:26
 * @description TestService
 */
@Service
@Slf4j
public class TestService {

    @Async()
    public void testAsync() {
        log.info("ceshi");
    }
}
