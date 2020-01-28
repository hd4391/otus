package hw.classes;

import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {
    @DefaultValue("8086")
    int port();

    @DefaultValue("http://192.168.99.100")
    String hostname();

    @DefaultValue("42")
    int maxThreads();
}