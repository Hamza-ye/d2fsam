//package org.nmcpye.am.dxf2.metadata.objectbundle.hooks.nmcphooks;
//
//import lombok.AllArgsConstructor;
//import org.nmcpye.am.common.enumeration.ProgramType;
//import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
//import org.nmcpye.am.dxf2.metadata.objectbundle.hooks.AbstractObjectBundleHook;
//import org.nmcpye.am.program.Program;
//import org.nmcpye.am.program.ProgramStage;
//import org.nmcpye.am.program.ProgramStageServiceExt;
//import org.nmcpye.am.security.acl.AccessStringHelper;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
///**
// * @author Hamza
// */
//// NMCP
//@Component
//@Profile({"testdev", "testprod"})
//@AllArgsConstructor
//public class ExtendedProgramObjectBundleHook extends AbstractObjectBundleHook<Program> {
//
//    private final ProgramStageServiceExt programStageService;
//
//    @Override
//    public void postCreate(Program object, ObjectBundle bundle) {
//        syncSharingForEventProgram(object, bundle);
//    }
//
//    @Override
//    public void postUpdate(Program object, ObjectBundle bundle) {
//        syncSharingForEventProgram(object, bundle);
//    }
//
//    private void syncSharingForEventProgram(Program program, ObjectBundle bundle) {
//        if (ProgramType.WITHOUT_REGISTRATION != program.getProgramType() || program.getProgramStages().isEmpty()) {
//            return;
//        }
//
//        ProgramStage programStage =
//            bundle.getPreheat().get(bundle.getPreheatIdentifier(), ProgramStage.class,
//                program.getProgramStages().iterator().next());
//
//        AccessStringHelper.copySharing(program, programStage);
//
//        programStage.setCreatedBy(program.getCreatedBy());
//        programStageService.updateProgramStage(programStage);
//    }
//}
