package org.nmcpye.am.scheduling.parameters.jackson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.nmcpye.am.scheduling.parameters.MetadataSyncJobParameters;

public class MetadataSyncJobParametersDeserializer extends AbstractJobParametersDeserializer<MetadataSyncJobParameters> {

    public MetadataSyncJobParametersDeserializer() {
        super(MetadataSyncJobParameters.class, CustomJobParameters.class);
    }

    @JsonDeserialize
    private static class CustomJobParameters extends MetadataSyncJobParameters {
    }
}
