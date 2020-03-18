package telegramApp.configuration;

import org.hibernate.dialect.MySQL5Dialect;
import org.springframework.stereotype.Component;

@Component
public class MySQL5DialectConfig extends MySQL5Dialect {
    @Override
    public String getTableTypeString() {
        return "ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}
