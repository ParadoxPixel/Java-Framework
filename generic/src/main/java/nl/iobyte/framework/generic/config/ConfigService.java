package nl.iobyte.framework.generic.config;

import nl.iobyte.framework.generic.config.interfaces.IConfig;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.structures.omap.ObjectMap;

public class ConfigService extends ObjectMap<String, IConfig> implements Service {

}
