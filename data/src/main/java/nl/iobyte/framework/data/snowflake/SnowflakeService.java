package nl.iobyte.framework.data.snowflake;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import de.mkammerer.snowflakeid.options.Options;
import de.mkammerer.snowflakeid.structure.Structure;
import de.mkammerer.snowflakeid.time.MonotonicTimeSource;
import nl.iobyte.framework.generic.config.ConfigService;
import nl.iobyte.framework.generic.config.interfaces.IConfig;
import nl.iobyte.framework.generic.service.annotations.Inject;
import nl.iobyte.framework.generic.service.interfaces.Service;

import java.time.Instant;

public class SnowflakeService implements Service {

    @Inject
    private ConfigService configService;
    private SnowflakeIdGenerator generator;

    @Override
    public void start() {
        IConfig config = configService.get("snowflake");
        if(config == null) {
            this.generator = SnowflakeIdGenerator.createDefault(420);
            return;
        }

        this.generator = SnowflakeIdGenerator.createCustom(
                config.getLong("id"),
                new MonotonicTimeSource(Instant.ofEpochMilli(config.getLong("epoch"))),
                Structure.createDefault(),
                Options.createDefault()
        );
    }

    /**
     * Generate snowflake id
     *
     * @return new snowflake id
     */
    public long nextId() {
        return generator.next();
    }

}
