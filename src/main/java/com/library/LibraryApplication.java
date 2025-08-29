package com.library;

import com.library.db.AuthorDAO;
import com.library.db.BookDAO;
import com.library.resources.AuthorResource;
import com.library.resources.BookResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryApplication extends Application<LibraryConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryApplication.class);

    public static void main(String[] args) throws Exception {
        new LibraryApplication().run(args);
    }

    @Override
    public String getName() {
        return "Library Management System";
    }

    @Override
    public void initialize(Bootstrap<LibraryConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        // Add migrations bundle
        bootstrap.addBundle(new MigrationsBundle<LibraryConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(LibraryConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(LibraryConfiguration configuration, Environment environment) {
        LOGGER.info("Starting Library Management System");
        
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
        
        // Create DAOs
        final AuthorDAO authorDAO = jdbi.onDemand(AuthorDAO.class);
        final BookDAO bookDAO = jdbi.onDemand(BookDAO.class);
        
        // Register resources
        environment.jersey().register(new AuthorResource(authorDAO));
        environment.jersey().register(new BookResource(bookDAO, authorDAO));
        
        // Health checks
        environment.healthChecks().register("database", new DatabaseHealthCheck(jdbi));
        
        LOGGER.info("Library Management System started successfully");
    }
}