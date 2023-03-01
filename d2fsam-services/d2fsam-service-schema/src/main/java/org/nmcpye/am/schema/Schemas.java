package org.nmcpye.am.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Lists;
import org.nmcpye.am.common.DxfNamespaces;

import java.util.List;

@JacksonXmlRootElement(localName = "schemas", namespace = DxfNamespaces.DXF_2_0)
public class Schemas {

    private List<Schema> schemas = Lists.newArrayList();

    public Schemas() {}

    public Schemas(List<Schema> schemas) {
        this.schemas = schemas;
    }

    @JsonProperty
    //    @JacksonXmlProperty( localName = "schema", namespace = DxfNamespaces.DXF_2_0 )
    //    @JacksonXmlElementWrapper( localName = "schemas", namespace = DxfNamespaces.DXF_2_0, useWrapping = false )
    public List<Schema> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
    }
}
