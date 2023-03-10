/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.nmcpye.am.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.nmcpye.am.common.*;
import org.nmcpye.am.common.AggregationType;
import org.nmcpye.am.legend.LegendSet;
import org.nmcpye.am.schema.PropertyType;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;

import java.util.List;

import static org.nmcpye.am.common.DimensionalObjectUtils.COMPOSITE_DIM_OBJECT_PLAIN_SEP;

/**
 * @author Lars Helge Overland
 */
@JacksonXmlRootElement(localName = "programAttributeDimension", namespace = DxfNamespaces.DXF_2_0)
public class
ProgramTrackedEntityAttributeDimensionItem
    extends BaseDimensionalItemObject implements EmbeddedObject {
    private Program program;

    private TrackedEntityAttribute attribute;

    public ProgramTrackedEntityAttributeDimensionItem() {
    }

    public ProgramTrackedEntityAttributeDimensionItem(Program program, TrackedEntityAttribute attribute) {
        this.program = program;
        this.attribute = attribute;
    }

    // -------------------------------------------------------------------------
    // DimensionalItemObject
    // -------------------------------------------------------------------------

    @Override
    public String getDimensionItem() {
        return program.getUid() + COMPOSITE_DIM_OBJECT_PLAIN_SEP + attribute.getUid();
    }

    @Override
    public String getDimensionItem(IdScheme idScheme) {
        return program.getPropertyValue(idScheme) + COMPOSITE_DIM_OBJECT_PLAIN_SEP
            + attribute.getPropertyValue(idScheme);
    }

    @Override
    public DimensionItemType getDimensionItemType() {
        return DimensionItemType.PROGRAM_ATTRIBUTE;
    }

    @Override
    public List<LegendSet> getLegendSets() {
        return attribute.getLegendSets();
    }

    @Override
    public AggregationType getAggregationType() {
        return attribute.getAggregationType();
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("program", program)
            .add("attribute", attribute).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(program, attribute);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (!getClass().isAssignableFrom(object.getClass())) {
            return false;
        }

        ProgramTrackedEntityAttributeDimensionItem other = (ProgramTrackedEntityAttributeDimensionItem) object;

        return Objects.equal(attribute, other.attribute) && Objects.equal(program, other.program);
    }

    // -------------------------------------------------------------------------
    // Get and set methods
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.REFERENCE, required = Property.Value.TRUE)
    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    @Property(value = PropertyType.REFERENCE, required = Property.Value.TRUE)
    public TrackedEntityAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(TrackedEntityAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getName() {
        return program.getName() + " " + attribute.getName();
    }

    @Override
    public String getDisplayName() {
        return program.getDisplayName() + " " + attribute.getDisplayName();
    }
}
