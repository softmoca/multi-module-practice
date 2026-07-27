package roomescape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The root package stays 'roomescape' in every module, so component scanning
 * still finds roomescape.db beans once they appear on the runtime classpath.
 * If you renamed the packages per module you would need scanBasePackages.
 */
@SpringBootApplication
public class RoomescapeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoomescapeApplication.class, args);
    }
}
