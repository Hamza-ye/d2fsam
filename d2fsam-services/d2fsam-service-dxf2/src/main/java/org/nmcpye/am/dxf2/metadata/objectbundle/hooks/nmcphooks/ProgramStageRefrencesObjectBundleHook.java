//package org.nmcpye.am.dxf2.metadata.objectbundle.hooks.nmcphooks;
//
//import lombok.AllArgsConstructor;
//import org.nmcpye.am.dxf2.metadata.objectbundle.ObjectBundle;
//import org.nmcpye.am.dxf2.metadata.objectbundle.hooks.AbstractObjectBundleHook;
//import org.nmcpye.am.program.Program;
//import org.nmcpye.am.program.ProgramStage;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
///**
// * @author Hamza
// */
//@Component
//@Profile({"testdev", "testprod"})
//@AllArgsConstructor
//public class ProgramStageRefrencesObjectBundleHook extends AbstractObjectBundleHook<ProgramStage> {
//    @Override
//    public void postCommit(ObjectBundle bundle) {
//
//        Iterable<ProgramStage> objects = bundle.getObjects(ProgramStage.class);
//        Map<String, Map<String, Object>> programStageReferences = bundle.getObjectReferences(ProgramStage.class);
//
//        if (programStageReferences == null || programStageReferences.isEmpty()) {
//            return;
//        }
//
//        for (ProgramStage identifiableObject : objects) {
//            ProgramStage programStage = identifiableObject;
//
//            programStage = bundle.getPreheat().get(bundle.getPreheatIdentifier(), programStage);
//
//            Map<String, Object> programStageReferenceMap = programStageReferences.get(identifiableObject.getUid());
//
//            if (programStage == null || programStageReferenceMap == null || programStageReferenceMap.isEmpty()) {
//                continue;
//            }
//
//            Program program = (Program) programStageReferenceMap.get("program");
//            programStage.setProgram(program);
//
//            preheatService.connectReferences(programStage, bundle.getPreheat(), bundle.getPreheatIdentifier());
//
//            getSession().update(programStage);
//        }
//    }
//}
