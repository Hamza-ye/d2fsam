package org.nmcpye.am.schema;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * The {@link PropertyIntrospectorService} is the {@link Property} introspection
 * provider.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@FunctionalInterface
public interface PropertyIntrospectorService {
    /**
     * Returns properties as a map property-name => Property class
     *
     * @param klass Class to get properties for
     * @return Map with key property-name and value Property
     */
    Map<String, Property> getPropertiesMap(Class<?> klass);

    /**
     * Returns all exposed properties on wanted class.
     *
     * @param klass Class to get properties for
     * @return List of properties for Class
     */
    default List<Property> getProperties(Class<?> klass) {
        return Lists.newArrayList(getPropertiesMap(klass).values());
    }
}
