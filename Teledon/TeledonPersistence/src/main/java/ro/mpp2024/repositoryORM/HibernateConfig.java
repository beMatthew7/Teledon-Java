package ro.mpp2024.repositoryORM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.util.Properties;

@Configuration
public class HibernateConfig {

    @Autowired
    private Properties props;

    @PostConstruct
    public void init() {
        HibernateUtils.setProperties(props);
    }
}