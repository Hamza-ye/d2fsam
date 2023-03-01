package org.nmcpye.am.trackedentityattributevalue;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class TrackedEntityAttributeValueId implements Serializable {
//    @Column(name = "trackedentityinstanceid")
//    private TrackedEntityInstance entityInstance;

    //    @Id
//    @Column(name = "trackedentityinstanceid")
    private Long entityInstance;

//    @Column(name = "trackedentityattributeid")
//    private TrackedEntityAttribute attribute;

    //    @Column(name = "trackedentityattributeid")
    private Long attribute;
}
