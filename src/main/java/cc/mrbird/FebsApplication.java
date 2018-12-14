package cc.mrbird;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("cc.mrbird.*.dao")
@EnableCaching
public class FebsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FebsApplication.class, args);
    }
}
