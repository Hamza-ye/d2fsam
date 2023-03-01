package org.nmcpye.am.scheduling;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.nmcpye.am.common.BaseIdentifiableObject;
import org.nmcpye.am.common.DxfNamespaces;
import org.nmcpye.am.common.SecondaryMetadataObject;
import org.nmcpye.am.hibernate.jsonb.type.JsonJobParametersType;
import org.nmcpye.am.scheduling.parameters.*;
import org.nmcpye.am.scheduling.parameters.jackson.JobConfigurationSanitizer;
import org.nmcpye.am.schema.annotation.Property;
import org.nmcpye.am.user.User;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;

import static org.nmcpye.am.scheduling.JobStatus.DISABLED;
import static org.nmcpye.am.scheduling.JobStatus.SCHEDULED;
import static org.nmcpye.am.schema.annotation.Property.Value.FALSE;

/**
 * This class defines configuration for a job in the system. The job is defined with general identifiers, as well as job
 * specific, such as jobType {@link JobType}.
 * <p>
 * All system jobs should be included in JobType enum and can be scheduled/executed with {@link SchedulingManager}.
 * <p>
 * The class uses a custom deserializer to handle several potential {@link JobParameters}.
 * <p>
 * Note that this class uses {@link JobConfigurationSanitizer} for serialization which needs to be update when new
 * properties are added.
 */
@Entity
@Table(name = "job_configuration")
@TypeDef(
    name = "jbJobParameters",
    typeClass = JsonJobParametersType.class,
    parameters = {@Parameter(name = "clazz", value = "org.nmcpye.am.scheduling.JobParameters")}
)
@JacksonXmlRootElement(localName = "jobConfiguration", namespace = DxfNamespaces.DXF_2_0)
@JsonDeserialize(converter = JobConfigurationSanitizer.class)
public class JobConfiguration extends BaseIdentifiableObject implements SecondaryMetadataObject {

    // -------------------------------------------------------------------------
    // Externally configurable properties
    // -------------------------------------------------------------------------

    ////////////////////////
    ///
    /// Common Columns
    ///
    ////////////////////////

    @Id
    @GeneratedValue
    @Column(name = "jobconfigurationid")
    private Long id;

    @NotNull
    @Size(max = 11)
    @Column(name = "uid", length = 11, nullable = false, unique = true)
    private String uid;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastupdatedby")
    private User updatedBy;

    ////////////////////////
    ///
    ///
    ////////////////////////

    /**
     * The type of job.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType jobType;

    /**
     * The cron expression used for scheduling the job. Relevant for scheduling
     * type {@link SchedulingType#CRON}.
     */
    @Column(name = "cron_expression")
    private String cronExpression;

    /**
     * The delay in seconds between the completion of one job execution and the
     * start of the next. Relevant for scheduling type {@link SchedulingType#FIXED_DELAY}.
     */
    @Column(name = "delay")
    private Integer delay;

    /**
     * Parameters of the job. Jobs can use their own implementation of the {@link JobParameters} class.
     */
    @Type(type = "jbJobParameters")
    @Column(name = "job_parameters", columnDefinition = "jsonb")
    private JobParameters jobParameters;

    /**
     * Indicates whether this job is currently enabled or disabled.
     */
    @Column(name = "enabled")
    private boolean enabled = true;

    // -------------------------------------------------------------------------
    // Internally managed properties
    // -------------------------------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(name = "job_status")
    private JobStatus jobStatus;

    @Column(name = "next_execution_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextExecutionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_executed_status")
    private JobStatus lastExecutedStatus = JobStatus.NOT_STARTED;

    @Column(name = "last_executed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastExecuted;

    @Column(name = "last_runtime_executed")
    private String lastRuntimeExecution;

    @Column(name = "in_memory_job")
    private boolean inMemoryJob = false;

    @Column(name = "user_uid")
    private String userUid;

    @Column(name = "leader_only_job")
    private boolean leaderOnlyJob = false;

    public JobConfiguration() {
    }

    /**
     * Constructor.
     *
     * @param name        the job name.
     * @param jobType     the {@link JobType}.
     * @param userUid     the user UID.
     * @param inMemoryJob whether this is an in-memory job.
     */
    public JobConfiguration(String name, JobType jobType, String userUid, boolean inMemoryJob) {
        this.name = name;
        this.jobType = jobType;
        this.userUid = userUid;
        this.inMemoryJob = inMemoryJob;
        init();
    }

    /**
     * Constructor which implies enabled true and in-memory job false.
     *
     * @param name           the job name.
     * @param jobType        the {@link JobType}.
     * @param cronExpression the cron expression.
     * @param jobParameters  the job parameters.
     */
    public JobConfiguration(String name, JobType jobType, String cronExpression, JobParameters jobParameters) {
        this(name, jobType, cronExpression, jobParameters, true, false);
    }

    /**
     * Constructor.
     *
     * @param name           the job name.
     * @param jobType        the {@link JobType}.
     * @param cronExpression the cron expression.
     * @param jobParameters  the job parameters.
     * @param enabled        whether this job is enabled.
     * @param inMemoryJob    whether this is an in-memory job.
     */
    public JobConfiguration(
        String name,
        JobType jobType,
        String cronExpression,
        JobParameters jobParameters,
        boolean enabled,
        boolean inMemoryJob
    ) {
        this.name = name;
        this.cronExpression = cronExpression;
        this.jobType = jobType;
        this.jobParameters = jobParameters;
        this.enabled = enabled;
        this.inMemoryJob = inMemoryJob;
        setJobStatus(enabled ? SCHEDULED : DISABLED);
        init();
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    private void init() {
        if (inMemoryJob) {
            setAutoFields();
        }
    }

    /**
     * Checks if this job has changes compared to the specified job configuration that are only
     * allowed for configurable jobs.
     *
     * @param other the job configuration that should be checked.
     * @return <code>true</code> if this job configuration has changes in fields that are only
     * allowed for configurable jobs, <code>false</code> otherwise.
     */
    public boolean hasNonConfigurableJobChanges(@Nonnull JobConfiguration other) {
        if (this.jobType != other.getJobType()) {
            return true;
        }
        if (this.jobStatus != other.getJobStatus()) {
            return true;
        }
        if (this.jobParameters != other.getJobParameters()) {
            return true;
        }
        return this.enabled != other.isEnabled();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isConfigurable() {
        return jobType.isConfigurable();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public SchedulingType getSchedulingType() {
        return jobType.getSchedulingType();
    }

    public boolean hasCronExpression() {
        return cronExpression != null && !cronExpression.isEmpty();
    }

    @Override
    public String toString() {
        return (
            "JobConfiguration{" +
                "uid='" +
                uid +
                '\'' +
                ", name='" +
                name +
                '\'' +
                ", jobType=" +
                jobType +
                ", cronExpression='" +
                cronExpression +
                '\'' +
                ", delay='" +
                delay +
                '\'' +
                ", jobParameters=" +
                jobParameters +
                ", enabled=" +
                enabled +
                ", inMemoryJob=" +
                inMemoryJob +
                ", lastRuntimeExecution='" +
                lastRuntimeExecution +
                '\'' +
                ", userUid='" +
                userUid +
                '\'' +
                ", leaderOnlyJob=" +
                leaderOnlyJob +
                ", jobStatus=" +
                jobStatus +
                ", nextExecutionTime=" +
                nextExecutionTime +
                ", lastExecutedStatus=" +
                lastExecutedStatus +
                ", lastExecuted=" +
                lastExecuted +
                '}'
        );
    }

    // -------------------------------------------------------------------------
    // Get and set methods
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonTypeId
    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    @JsonProperty
    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @JsonProperty
    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    /**
     * The sub type names refer to the {@link JobType} enumeration. Defaults to null for unmapped job types.
     */
    @JsonProperty
    @Property(required = FALSE)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "jobType", defaultImpl = Void.class)
    @JsonSubTypes(
        value = {
//            @JsonSubTypes.Type( value = AnalyticsJobParameters.class, name = "ANALYTICS_TABLE" ),
//            @JsonSubTypes.Type( value = ContinuousAnalyticsJobParameters.class, name = "CONTINUOUS_ANALYTICS_TABLE" ),
            @JsonSubTypes.Type(value = MonitoringJobParameters.class, name = "MONITORING"),
//            @JsonSubTypes.Type( value = PredictorJobParameters.class, name = "PREDICTOR" ),
//            @JsonSubTypes.Type( value = PushAnalysisJobParameters.class, name = "PUSH_ANALYSIS" ),
//            @JsonSubTypes.Type( value = SmsJobParameters.class, name = "SMS_SEND" ),
            @JsonSubTypes.Type(value = MetadataSyncJobParameters.class, name = "META_DATA_SYNC"),
            @JsonSubTypes.Type(value = EventProgramsDataSynchronizationJobParameters.class, name = "EVENT_PROGRAMS_DATA_SYNC"),
            @JsonSubTypes.Type(value = TrackerProgramsDataSynchronizationJobParameters.class, name = "TRACKER_PROGRAMS_DATA_SYNC"),
//            @JsonSubTypes.Type( value = DataSynchronizationJobParameters.class, name = "DATA_SYNC" ),
//            @JsonSubTypes.Type( value = DisableInactiveUsersJobParameters.class, name = "DISABLE_INACTIVE_USERS" ),
            @JsonSubTypes.Type(value = TrackerTrigramIndexJobParameters.class, name = "TRACKER_SEARCH_OPTIMIZATION")//,
//            @JsonSubTypes.Type( value = DataIntegrityJobParameters.class, name = "DATA_INTEGRITY" ),
//            @JsonSubTypes.Type( value = AggregateDataExchangeJobParameters.class, name = "AGGREGATE_DATA_EXCHANGE" )
        }
    )
    public JobParameters getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }

    @JsonProperty
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Date getNextExecutionTime() {
        return nextExecutionTime;
    }

    /**
     * Only set next execution time if the job is not continuous.
     */
    public void setNextExecutionTime(Date nextExecutionTime) {
        if (cronExpression == null || cronExpression.equals("") || cronExpression.equals("* * * * * ?")) {
            return;
        }

        if (nextExecutionTime != null) {
            this.nextExecutionTime = nextExecutionTime;
        } else {
            this.nextExecutionTime = new CronTrigger(cronExpression).nextExecutionTime(new SimpleTriggerContext());
        }
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Date getLastExecuted() {
        return lastExecuted;
    }

    public void setLastExecuted(Date lastExecuted) {
        this.lastExecuted = lastExecuted;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public JobStatus getLastExecutedStatus() {
        return lastExecutedStatus;
    }

    public void setLastExecutedStatus(JobStatus lastExecutedStatus) {
        this.lastExecutedStatus = lastExecutedStatus;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getLastRuntimeExecution() {
        return lastRuntimeExecution;
    }

    public void setLastRuntimeExecution(String lastRuntimeExecution) {
        this.lastRuntimeExecution = lastRuntimeExecution;
    }

    @JsonProperty
    public boolean isLeaderOnlyJob() {
        return leaderOnlyJob;
    }

    public void setLeaderOnlyJob(boolean leaderOnlyJob) {
        this.leaderOnlyJob = leaderOnlyJob;
    }

    public boolean isInMemoryJob() {
        return inMemoryJob;
    }

    public void setInMemoryJob(boolean inMemoryJob) {
        this.inMemoryJob = inMemoryJob;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    //    @Override
    public Long getId() {
        return id;
    }

    //    @Override
    public void setId(Long id) {
        this.id = id;
    }

    //    @Override
    public String getUid() {
        return uid;
    }

    //    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    //    @Override
    public String getCode() {
        return code;
    }

    //    @Override
    public void setCode(String code) {
        this.code = code;
    }

    //    @Override
    public String getName() {
        return name;
    }

    //    @Override
    public void setName(String name) {
        this.name = name;
    }

    //    @Override
    public Instant getCreated() {
        return created;
    }

    //    @Override
    public void setCreated(Instant created) {
        this.created = created;
    }

    //    @Override
    public Instant getUpdated() {
        return updated;
    }

    //    @Override
    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    //    @Override
    public User getUpdatedBy() {
        return updatedBy;
    }

    //    @Override
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}
