package util;

import com.google.inject.AbstractModule;
import net.jmob.guice.conf.core.ConfigurationModule;

import java.io.File;

public class DataSourceModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ConfigurationModule().fromPath(new File("./src/main/resources/")));
    }
}
