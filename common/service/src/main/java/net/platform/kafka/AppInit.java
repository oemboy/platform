
package net.platform.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public class AppInit implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("App init.");
    }
}
