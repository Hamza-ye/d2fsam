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
package org.nmcpye.am.programrule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.*;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.MetadataObject;
import org.nmcpye.am.common.ValueType;
import org.nmcpye.am.dataelement.DataElement;
import org.nmcpye.am.hibernate.jsonb.type.JsonSetBinaryType;
import org.nmcpye.am.program.Program;
import org.nmcpye.am.program.ProgramStage;
import org.nmcpye.am.trackedentity.TrackedEntityAttribute;
import org.nmcpye.am.translation.Translation;
import org.nmcpye.am.user.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * @author markusbekken
 */
@Entity
@Table(name = "program_rule_variable")
@TypeDefs(
    {
        @TypeDef(
            name = "jblTranslations",
            typeClass = JsonSetBinaryType.class,
            parameters = {
                @org.hibernate.annotations.Parameter(name = "clazz",
                    value = "org.nmcpye.am.translation.Translation"),
            }
        )
    }
)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@JacksonXmlRootElement(localName = "programRuleVariable", namespace = DxfNamespaces.DXF_2_0)
public class ProgramRuleVariable
    extends BaseIdentifiableObject implements MetadataObject {

    @Id
    @GeneratedValue
    @Column(name = "programrulevariableid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    /**
     * The program that the variable belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programid")
    private Program program;

    /**
     * The source of the variables content. Allowed values are:
     * dataelement_newest_event_program_stage Get a specific data elements value
     * from the most recent event in the current enrollment, but within one
     * program stage. dataelement_uID and programstage_uID needs to be
     * specified. dataelement_newest_event_program Get a specific data elements
     * value from the most recent event in the current enrollment, regardless of
     * program stage.datalement_uID needs to be specified.
     * dataelement_current_event Get a specific data elements value, but only
     * within the current event. dataelement_previous_event Get a specific data
     * elements value, specifically from the event preceding the current event,
     * if this exists. calculated_value Do not assign the variable a hard-linked
     * source, it will be populated by rules with assignvariable actions(i.e.
     * calculation rules). tei_attribute Get a specific attribute from the
     * current tracked entity. the linked attribute will be used to lookup the
     * attributes uID value.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sourcetype")
    private ProgramRuleVariableSourceType sourceType;

    /**
     * Used for sourceType tei_attribute to determine which attribute to fetch
     * into the variable.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trackedentityattributeid")
    private TrackedEntityAttribute attribute;

    /**
     * The data element that is linked to the variable. Must be defined if the
     * sourceType is one of the following:
     * <p/>
     * <ul>
     * <li>dataelement_newest_event_program_stage</li>
     * <li>dataelement_newest_event_program</li>
     * <li>dataelement_current_even</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataelementid")
    private DataElement dataElement;

    /**
     * If the dataElement or trackedEntityAttribute is connected to an option
     * set, use this option sets code(and not the name) as value
     */
    @Column(name = "usecodeforoptionset")
    private boolean useCodeForOptionSet;

    /**
     * Specification of the program stage that the variable should be fetched
     * from. Only used for source type dataelement_newest_event_program_stage
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programstageid")
    private ProgramStage programStage;

    /**
     * Specify ValueType for CALCULATED_VALUE ProgramRuleVariable. For other
     * sourceTypes, valuetype will be fetched from attached DataElement or
     * DataElement. If non of these parameters are provided then
     * ValueType.TEXT will be taken as default.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "valuetype")
    private ValueType valueType = ValueType.TEXT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    @Immutable
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    /**
     * Set of available object translation, normally filtered by locale.
     */
    @Type(type = "jblTranslations")
    @Column(name = "translations", columnDefinition = "jsonb")
    protected Set<Translation> translations = new HashSet<>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramRuleVariable() {

    }

    public ProgramRuleVariable(String name,
                               Program program,
                               ProgramRuleVariableSourceType sourceType,
                               TrackedEntityAttribute attribute,
                               DataElement dataElement,
                               boolean useCodeForOptionSet,
                               ProgramStage programStage, ValueType valueType) {
        this.name = name;
        this.program = program;
        this.sourceType = sourceType;
        this.attribute = attribute;
        this.dataElement = dataElement;
        this.useCodeForOptionSet = useCodeForOptionSet;
        this.programStage = programStage;
        this.valueType = valueType;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------


    @JsonIgnore
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    @Override
    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public Instant getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public boolean isUseCodeForOptionSet() {
        return useCodeForOptionSet;
    }

    @Override
    public User getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public User getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Set<Translation> getTranslations() {
        if (this.translations == null) {
            this.translations = new HashSet<>();
        }

        return translations;
    }

    /**
     * Clears out cache when setting translations.
     */
    public void setTranslations(Set<Translation> translations) {
        this.translationCache.clear();
        this.translations = translations;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @JsonProperty
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean getUseCodeForOptionSet() {
        return useCodeForOptionSet;
    }

    public void setUseCodeForOptionSet(boolean useCodeForOptionSet) {
        this.useCodeForOptionSet = useCodeForOptionSet;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public ProgramStage getProgramStage() {
        return programStage;
    }

    public void setProgramStage(ProgramStage programStage) {
        this.programStage = programStage;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DataElement getDataElement() {
        return dataElement;
    }

    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement;
    }

    @JsonProperty("trackedEntityAttribute")
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JacksonXmlProperty(localName = "trackedEntityAttribute", namespace = DxfNamespaces.DXF_2_0)
    public TrackedEntityAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(TrackedEntityAttribute attribute) {
        this.attribute = attribute;
    }

    @JsonProperty("programRuleVariableSourceType")
    @JacksonXmlProperty(localName = "programRuleVariableSourceType", namespace = DxfNamespaces.DXF_2_0)
    public ProgramRuleVariableSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(ProgramRuleVariableSourceType sourceType) {
        this.sourceType = sourceType;
    }

    @JsonProperty("valueType")
    @JacksonXmlProperty(localName = "valueType", namespace = DxfNamespaces.DXF_2_0)
    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }
}
