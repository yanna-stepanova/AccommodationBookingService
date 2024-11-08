package stepanova.yana.config;

import java.util.Properties;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class CustomPostgreSqlContainer extends PostgreSQLContainer<CustomPostgreSqlContainer> {
    private static final DockerImageName DB_IMAGE = DockerImageName.parse("postgres");
    private static CustomPostgreSqlContainer mySqlContainer;

    public CustomPostgreSqlContainer(DockerImageName dockerImage) {
        super(dockerImage);
    }

    public static synchronized CustomPostgreSqlContainer getInstance() {
        if (mySqlContainer == null) {
            mySqlContainer = new CustomPostgreSqlContainer(DB_IMAGE);
        }
        return mySqlContainer;
    }

    @Override
    public void start() {
        super.start();
        Properties props = new Properties();
        props.put("TEST_DB_URL", mySqlContainer.getJdbcUrl());
        props.put("TEST_DB_USERNAME", mySqlContainer.getUsername());
        props.put("TEST_DB_PASSWORD", mySqlContainer.getPassword());
        System.setProperties(props);
    }

    @Override
    public void stop() {
    }
}
