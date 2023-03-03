CREATE SEQUENCE if not exists hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

create sequence if not exists datavalueaudit_sequence;
select setval('datavalueaudit_sequence', max(datavalueauditid))
FROM data_value_audit;

create sequence if not exists programstageinstance_sequence;
select setval('programstageinstance_sequence', max(programstageinstanceid))
FROM program_stage_instance;

create sequence if not exists trackedentitydatavalueaudit_sequence;
select setval('trackedentitydatavalueaudit_sequence', max(trackedentitydatavalueauditid))
FROM tracked_entity_data_value_audit;

create sequence if not exists trackedentityinstance_sequence;
select setval('trackedentityinstance_sequence', max(trackedentityinstanceid))
FROM tracked_entity_instance;

create sequence if not exists trackedentityinstanceaudit_sequence;
select setval('trackedentityinstanceaudit_sequence', max(trackedentityinstanceauditid))
FROM tracked_entity_instance_audit;

create sequence if not exists programinstance_sequence;
select setval('programinstance_sequence', max(programinstanceid))
FROM program_instance;

-- create sequence if not exists deletedobject_sequence;
-- select setval('deletedobject_sequence', max(deletedobjectid)) FROM deletedobject;

-- create sequence if not exists reservedvalue_sequence;
-- select setval('reservedvalue_sequence', max(reservedvalueid)) FROM reservedvalue;

create sequence if not exists usermessage_sequence;
select setval('usermessage_sequence', max(usermessageid))
FROM user_message;

create sequence if not exists messageconversation_sequence;
select setval('messageconversation_sequence', max(messageconversationid))
FROM message_conversation;

create sequence if not exists message_sequence;
select setval('message_sequence', max(messageid))
FROM message;
